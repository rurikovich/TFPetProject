package ru.rurik

import domain.{Expense, ExpenseTree}

import cats.{Applicative, Traverse}
import ru.rurik.Starter.expenseTree
//import interpreters.ExpenseRepoOptionInMemory.{getById, getByParentId}

import cats.Functor
import cats.implicits.catsSyntaxOptionId

import scala.collection.mutable


object algebras {

  trait ExpenseRepo[F[_]] {
    def getById(id: Long): F[Expense]

    def getByParentId(id: Long): F[List[Expense]]

    def create(expense: Expense): F[Expense]

    def update(expense: Expense): F[Expense]

    def delete(id: Long): F[Expense]

    def delete(ids: List[Long]): F[List[Expense]]
  }

  object ExpenseRepo {
    def apply[F[_] : ExpenseRepo]: ExpenseRepo[F] = implicitly
  }

  def getById[F[_] : ExpenseRepo](id: Long): F[Expense] = ExpenseRepo[F].getById(id)

  def getByParentId[F[_] : ExpenseRepo](id: Long): F[List[Expense]] = ExpenseRepo[F].getByParentId(id)

  def create[F[_] : ExpenseRepo](expense: Expense): F[Expense] = ExpenseRepo[F].create(expense)

  def update[F[_] : ExpenseRepo](expense: Expense): F[Expense] = ExpenseRepo[F].update(expense)

  def delete[F[_] : ExpenseRepo](id: Long): F[Expense] = ExpenseRepo[F].delete(id)

  def delete[F[_] : ExpenseRepo](ids: List[Long]): F[List[Expense]] = ExpenseRepo[F].delete(ids)

  trait Program[F[_]] {

    def flatMap[A, B](fa: F[A], afb: A => F[B]): F[B]

    def map[A, B](fa: F[A], ab: A => B): F[B]

    def fold[A, B, C](fa: F[A], first: B => C, second: A => C): C

  }

  object Program {
    def apply[F[_]](implicit Prog: Program[F]): Program[F] = Prog
  }

  implicit class ProgramSyntax[F[_], A](fa: F[A]) {
    def map[B](f: A => B)(implicit Prog: Program[F]): F[B] = Prog.map(fa, f)

    def flatMap[B](afb: A => F[B])(implicit Prog: Program[F]): F[B] = Prog.flatMap(fa, afb)

    def fold[B, C](first: B => C, second: A => C)(implicit Prog: Program[F]): C = Prog.fold(fa, first, second)
  }

}


object interpreters {

  import algebras._

  object ExpenseRepoOptionInMemory extends ExpenseRepo[Option] {

    val expenses = mutable.Map.empty[Long, Expense]

    override def getById(id: Long): Option[Expense] = expenses.get(id)

    override def getByParentId(id: Long): Option[List[Expense]] = expenses.values.filter(_.parentId.exists(_ == id)).toList.some

    override def create(expense: Expense): Option[Expense] = expenses.put(expense.id, expense)

    override def update(expense: Expense): Option[Expense] = expenses.put(expense.id, expense)

    override def delete(id: Long): Option[Expense] = expenses.remove(id)

    override def delete(ids: List[Long]): Option[List[Expense]] = {
      import cats.instances.list._
      import cats.instances.option._
      import cats.syntax.traverse._

      ids.map(expenses.remove).sequence
    }

  }

}


object Starter extends App {

  import algebras._

  def expenseTree[F[_] : Applicative : Program : ExpenseRepo](id: Long): F[ExpenseTree] =
    for {
      expense <- getById(id)
      subExpenses <- getByParentId(id)
      expenseTreeList <- {
        import cats.implicits._
        subExpenses.map(exp => expenseTree[F](exp.id)).sequence
      }
    } yield ExpenseTree(expense, Some(expenseTreeList))


  def expenseTable[F[_] : ExpenseRepo](expenseId: Long): Map[String, Double] = ???

}