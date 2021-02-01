package ru.rurik.infrastructure

import ru.rurik.domain.algebras.algebrasExt.{ExpenseStatsRepo, UserRepo}
import ru.rurik.domain.models.ExpenseCategory.ExpenseCategory
import ru.rurik.domain.models.{Expense, User}

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
