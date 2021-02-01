package ru.rurik

import domain.{Expense, ExpenseCategory}

import cats.implicits.catsSyntaxOptionId

object algebras {

  trait ExpenseRepo[F[_]] {
    def getById(id: Long): F[Expense]

    def getByParentId(id: Long): F[List[Expense]]

    def getUserExpenses(userId: Long): F[List[Expense]]

  }

  object ExpenseRepo {
    def apply[F[_] : ExpenseRepo]: ExpenseRepo[F] = implicitly
  }


  def getExpenseById[F[_] : ExpenseRepo](id: Long): F[Expense] = ExpenseRepo[F].getById(id)

  def getExpenseByParentId[F[_] : ExpenseRepo](id: Long): F[List[Expense]] = ExpenseRepo[F].getByParentId(id)

  def getUserExpenses[F[_] : ExpenseRepo](userId: Long): F[List[Expense]] = ExpenseRepo[F].getUserExpenses(userId)

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
