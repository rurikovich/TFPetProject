package ru.rurik

import algebrasExt.{ExpenseStatsRepo, UserRepo}
import domain.ExpenseCategory.ExpenseCategory
import domain.{Expense, ExpenseCategory, User}

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


object interpreterExt {

  import cats.implicits._

  implicit object ExpenseStatsRepoOption extends ExpenseStatsRepo[Option] {

    override def amountsByCategory(expenses: List[Expense]): Option[Map[ExpenseCategory, Long]] =
      expenses.foldLeft(Map.empty[ExpenseCategory, Long]) {
        (map: Map[ExpenseCategory, Long], expense) =>
          val category = expense.category
          val oldAmount: Long = map.getOrElse(category, 0)
          val amount = oldAmount + expense.amount
          (map - category) + (category -> amount)
      }.some
  }

  implicit object UserRepoOption extends UserRepo[Option] {
    val users = List(User(1L, "user1"))

    override def getAll: Option[List[User]] = users.some
  }

}