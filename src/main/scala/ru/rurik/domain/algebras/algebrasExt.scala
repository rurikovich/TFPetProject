package ru.rurik.domain.algebras

import ru.rurik.domain.models.ExpenseCategory.ExpenseCategory
import ru.rurik.domain.models.{Expense, User}

object algebrasExt {

  trait ExpenseStatsRepo[F[_]] {

    def amountsByCategory(expenses: List[Expense]): F[Map[ExpenseCategory, Long]]

  }

  object ExpenseStatsRepo {
    def apply[F[_] : ExpenseStatsRepo]: ExpenseStatsRepo[F] = implicitly
  }


  trait UserRepo[F[_]] {
    def getAll: F[List[User]]
  }

  object UserRepo {
    def apply[F[_] : UserRepo]: UserRepo[F] = implicitly
  }


  def getAllUsers[F[_] : UserRepo]: F[List[User]] = UserRepo[F].getAll


  def amountsByCategory[F[_] : ExpenseStatsRepo](expenses: List[Expense]): F[Map[ExpenseCategory, Long]] = ExpenseStatsRepo[F].amountsByCategory(expenses)

}
