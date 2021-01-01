package ru.rurik

import domain.ExpenseCategory.ExpenseCategory
import domain.{ExpenseTree, User}

import cats.Applicative

object Starter extends App {

  import algebras._


  def expenseTree[F[_] : Applicative : Program : ExpenseRepo](id: Long): F[ExpenseTree] =
    for {
      expense <- getExpenseById(id)
      subExpenses <- getExpenseByParentId(id)
      expenseTreeList <- {
        import cats.implicits._
        subExpenses.map(exp => expenseTree[F](exp.id)).sequence
      }
    } yield ExpenseTree(expense, Some(expenseTreeList))


  def userExpenseMaxStatistics[F[_] : Program : UserRepo : ExpenseRepo](): F[Map[ExpenseCategory, User]] = {

    for {
      users <- getAllUsers


    } yield Map.empty[ExpenseCategory, User]


  }

  import  interpreters._


  val maybeTree: Option[ExpenseTree] = expenseTree[Option](1)

  println(maybeTree)

}
