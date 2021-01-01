package ru.rurik

import domain.ExpenseCategory.ExpenseCategory
import domain.{Expense, ExpenseCategory, ExpenseTree, User}

import cats.Applicative
import cats.implicits.{catsSyntaxOptionId, toTraverseOps}





object algebras {

  trait ExpenseRepo[F[_]] {
    def getById(id: Long): F[Expense]

    def getByParentId(id: Long): F[List[Expense]]
  }

  object ExpenseRepo {
    def apply[F[_] : ExpenseRepo]: ExpenseRepo[F] = implicitly
  }




  trait UserExpenseRepo[F[_]] {
    def getUserExpenses: F[List[User]]
  }

  object UserExpenseRepo {
    def apply[F[_] : UserExpenseRepo]: UserExpenseRepo[F] = implicitly
  }


  trait UserRepo[F[_]] {
    def getAll: F[List[User]]
  }

  object UserRepo {
    def apply[F[_] : UserRepo]: UserRepo[F] = implicitly
  }

  def getExpenseById[F[_] : ExpenseRepo](id: Long): F[Expense] = ExpenseRepo[F].getById(id)

  def getExpenseByParentId[F[_] : ExpenseRepo](id: Long): F[List[Expense]] = ExpenseRepo[F].getByParentId(id)

  def getAllUsers[F[_] : UserRepo]: F[List[User]] = UserRepo[F].getAll

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
      1L -> Expense(1, "exp1", ExpenseCategory.Food, 100),
      2L -> Expense(2, "exp1", ExpenseCategory.Appliances, 100, 1L.some)
    )

    override def getById(id: Long): Option[Expense] = expenses.get(id)

    override def getByParentId(id: Long): Option[List[Expense]] = expenses.values.filter(_.parentId.exists(_ == id)).toList.some
  }

  implicit object ProgramOption extends Program[Option] {
    override def flatMap[A, B](fa: Option[A], afb: A => Option[B]): Option[B] = fa.flatMap(afb)

    override def map[A, B](fa: Option[A], ab: A => B): Option[B] = fa.map(ab)

    override def fold[A, B, C](fa: Option[A], first: B => C, second: A => C): C =
      fa.fold(first(UnknownError.asInstanceOf[B]))(second(_))
  }

}
