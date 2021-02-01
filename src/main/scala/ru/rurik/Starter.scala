package ru.rurik

import cats.Applicative
import ru.rurik.domain.ExpenseCategory.ExpenseCategory
import ru.rurik.domain.{ExpenseTree, User}
import cats.implicits.toTraverseOps

object Starter extends App {

  import algebras._
  import algebrasExt._

  def expenseTree[F[_] : Applicative : Program : ExpenseRepo](id: Long): F[ExpenseTree] =
    for {
      expense <- getExpenseById(id)
      subExpenses <- getExpenseByParentId(id)
      expenseTreeList <- {
        import cats.implicits._
        subExpenses.map(exp => expenseTree[F](exp.id)).sequence
      }
    } yield ExpenseTree(expense, Some(expenseTreeList))


  def userStats[F[_] : Applicative : Program : ExpenseRepo : UserRepo : ExpenseStatsRepo](): F[List[(User, Map[ExpenseCategory, Long])]] =
    for {
      users <- getAllUsers
      stats <- users.map { u =>
        for {
          expenses <- getUserExpenses(u.id)
          categoryAmount <- amountsByCategory(expenses)
        } yield (u, categoryAmount)
      }.sequence
    } yield stats


  import interpreters._
  val expenseTree: Option[ExpenseTree] = expenseTree[Option](1)
  println(expenseTree)


  import interpreterExt._
  val stats = userStats[Option]()
  println(stats)

}
