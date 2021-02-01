package ru.rurik.infrastructure

import ru.rurik.domain.models.{Expense, ExpenseCategory}

import cats.implicits.catsSyntaxOptionId

object interpreters {

  sealed trait AppError extends Throwable {
    def message: String
  }

  case object UnknownError extends AppError {
    override def message: String = s"Unexpected Error"
  }

  import ru.rurik.domain.algebras.algebras._

  implicit object ExpenseRepoOptionInMemory extends ExpenseRepo[Option] {

    val expenses: Map[Long, Expense] = Map(
      1L -> Expense(id = 1, name = "exp1", category = ExpenseCategory.Food, amount = 100, userId = 1L.some),
      2L -> Expense(2, "exp2", ExpenseCategory.Appliances, 100, 1L.some, userId = 1L.some),
      3L -> Expense(3, "exp3", ExpenseCategory.Appliances, 99, 1L.some, userId = 1L.some)
    )

    override def getById(id: Long): Option[Expense] = expenses.get(id)

    override def getByParentId(id: Long): Option[List[Expense]] =
      expenses.values.filter(_.parentId.exists(_ == id)).toList.some

    override def getUserExpenses(userId: Long): Option[List[Expense]] =
      expenses.values.filter(_.userId.contains(userId)).toList.some

  }

  implicit object ProgramOption extends Program[Option] {
    override def flatMap[A, B](fa: Option[A], afb: A => Option[B]): Option[B] = fa.flatMap(afb)

    override def map[A, B](fa: Option[A], ab: A => B): Option[B] = fa.map(ab)

    override def fold[A, B, C](fa: Option[A], first: B => C, second: A => C): C =
      fa.fold(first(UnknownError.asInstanceOf[B]))(second(_))
  }

}
