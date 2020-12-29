package ru.rurik

import domain.{Expense, ExpenseTree}

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

}


object interpreters {

  import algebras._

  implicit object ExpenseRepoOptionInMemory extends ExpenseRepo[Option] {

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
  import interpreters._

  val expenseRepo: ExpenseRepo[Option] = ExpenseRepo[Option]


  def expenseTree[F[_] : ExpenseRepo](expenseId: Long): ExpenseTree = ???

  def expenseTable[F[_] : ExpenseRepo](expenseId: Long): Map[String, Double] = ???

}