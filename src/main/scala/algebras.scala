package ru.rurik

import domain.{Expense, ExpenseCategory, ExpenseTree, Tree}

import cats.{Applicative, Traverse}
import ru.rurik.Starter.expenseTree
import ru.rurik.domain.ExpenseCategory.ExpenseCategory

import scala.util.chaining.scalaUtilChainingOps
//import interpreters.ExpenseRepoOptionInMemory.{getById, getByParentId}

import cats.Functor
import cats.implicits.catsSyntaxOptionId

import scala.collection.mutable


object algebras {

  trait ExpenseRepo[F[_]] {
    def getById(id: Long): F[Expense]

    def getByParentId(id: Long): F[List[Expense]]
  }

  object ExpenseRepo {
    def apply[F[_] : ExpenseRepo]: ExpenseRepo[F] = implicitly
  }

  def getById[F[_] : ExpenseRepo](id: Long): F[Expense] = ExpenseRepo[F].getById(id)

  def getByParentId[F[_] : ExpenseRepo](id: Long): F[List[Expense]] = ExpenseRepo[F].getByParentId(id)

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

  sealed trait AppError extends Throwable {
    def message: String
  }

  case object UnknownError extends AppError {
    override def message: String = s"Unexpected Error"
  }

  import algebras._

  implicit object ExpenseRepoOptionInMemory extends ExpenseRepo[Option] {

    val expenses: mutable.Map[Long, Expense] = mutable.Map(
      1L -> Expense(1, "exp1", ExpenseCategory.Food, 100),
      2L -> Expense(2, "exp1", ExpenseCategory.Appliances, 100, 1L.some)
    )

    override def getById(id: Long): Option[Expense] = expenses.get(id)

    override def getByParentId(id: Long): Option[List[Expense]] = expenses.values.filter(_.parentId.exists(_ == id)).toList.some
  }

  implicit object ProgramOption extends Program[Option] {
    override def flatMap[A, B](fa: Option[A], afb: A => Option[B]): Option[B] = fa.flatMap(afb)

    override def map[A, B](fa: Option[A], ab: A => B): Option[B] = fa.map(ab)

    override def fold[A, B, C](fa: Option[A], first: B => C, second: A => C): C =
      fa.fold(first(UnknownError.asInstanceOf[B]))(second(_))
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


  import interpreters._

  val maybeTree: Option[ExpenseTree] = expenseTree[Option](1)

  println(maybeTree)

}