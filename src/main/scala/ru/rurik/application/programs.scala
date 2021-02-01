package ru.rurik.application

import cats.Applicative
import ru.rurik.domain.models.ExpenseCategory.ExpenseCategory
import ru.rurik.domain.models.{ExpenseTree, User}

import cats.implicits.toTraverseOps

object programs {

  import ru.rurik.domain.algebras.algebras._
  import ru.rurik.domain.algebras.algebrasExt._

  def expenseTreeProgram[F[_] : Applicative : Program : ExpenseRepo](id: Long): F[ExpenseTree] =
    for {
      expense <- getExpenseById(id)
      subExpenses <- getExpenseByParentId(id)
      expenseTreeList <- {
        import cats.implicits._
        subExpenses.map(exp => expenseTreeProgram[F](exp.id)).sequence
      }
    } yield ExpenseTree(expense, Some(expenseTreeList))


  def userStatsProgram[F[_] : Applicative : Program : ExpenseRepo : UserRepo : ExpenseStatsRepo](): F[List[(User, Map[ExpenseCategory, Long])]] =
    for {
      users <- getAllUsers
      stats <- users.map {
        u =>
          for {
            expenses <- getUserExpenses(u.id)
            categoryAmount <- amountsByCategory(expenses)
          } yield (u, categoryAmount)
      }.sequence
    } yield stats


}
