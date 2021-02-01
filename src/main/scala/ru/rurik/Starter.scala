package ru.rurik

import ru.rurik.domain.models.ExpenseTree

object Starter extends App {

  import ru.rurik.application.programs._
  import ru.rurik.infrastructure.interpreters._
  import ru.rurik.infrastructure.interpreterExt._

  val expenseTree: Option[ExpenseTree] = expenseTreeProgram[Option](1)
  println(expenseTree)


  val stats = userStatsProgram[Option]()
  println(stats)

}
