package ru.rurik.domain.algebras

import cats.tagless.finalAlg
import ru.rurik.domain.models.ExpenseCategory.ExpenseCategory
import ru.rurik.domain.models.{Expense, User}

import scala.language.higherKinds

object algebrasExt {

  @finalAlg
  trait ExpenseStatsRepo[F[_]] {

    def amountsByCategory(expenses: List[Expense]): F[Map[ExpenseCategory, Long]]

  }

  @finalAlg
  trait UserRepo[F[_]] {
    def getAll: F[List[User]]
  }

  def getAllUsers[F[_] : UserRepo]: F[List[User]] = UserRepo[F].getAll


  def amountsByCategory[F[_] : ExpenseStatsRepo](expenses: List[Expense]): F[Map[ExpenseCategory, Long]] = ExpenseStatsRepo[F].amountsByCategory(expenses)

}
