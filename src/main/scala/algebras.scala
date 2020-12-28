package ru.rurik

object algebras {

  import cats.tagless._

  @autoFunctorK
  trait ExpenseRepo[F[_]] {
    def getById(id: Long): F[Expense]

    def getByParentId(id: Long): F[List[Expense]]

    def create(expense: Expense): F[Expense]

    def update(expense: Expense): F[Option[Expense]]

    def delete(id: Long): F[Int]

    def delete(ids: List[Long]): F[Int]
  }

}
