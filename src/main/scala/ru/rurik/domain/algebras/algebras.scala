package ru.rurik.domain.algebras

import cats.tagless.{autoFunctorK, finalAlg}
import ru.rurik.domain.models.Expense

object algebras {

  @finalAlg
   trait ExpenseRepo[F[_]] {
    def getById(id: Long): F[Expense]

    def getByParentId(id: Long): F[List[Expense]]

    def getUserExpenses(userId: Long): F[List[Expense]]

  }


  def getExpenseById[F[_] : ExpenseRepo](id: Long): F[Expense] = ExpenseRepo[F].getById(id)

  def getExpenseByParentId[F[_] : ExpenseRepo](id: Long): F[List[Expense]] = ExpenseRepo[F].getByParentId(id)

  def getUserExpenses[F[_] : ExpenseRepo](userId: Long): F[List[Expense]] = ExpenseRepo[F].getUserExpenses(userId)

  @finalAlg
  trait Program[F[_]] {

    def flatMap[A, B](fa: F[A], afb: A => F[B]): F[B]

    def map[A, B](fa: F[A], ab: A => B): F[B]

    def fold[A, B, C](fa: F[A], first: B => C, second: A => C): C

  }


  implicit class ProgramSyntax[F[_], A](fa: F[A]) {
    def map[B](f: A => B)(implicit Prog: Program[F]): F[B] = Prog.map(fa, f)

    def flatMap[B](afb: A => F[B])(implicit Prog: Program[F]): F[B] = Prog.flatMap(fa, afb)

    def fold[B, C](first: B => C, second: A => C)(implicit Prog: Program[F]): C = Prog.fold(fa, first, second)
  }

}
