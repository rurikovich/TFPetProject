package ru.rurik

object ExpenseCategory extends Enumeration {
  type ExpenseCategory = Value
  val Food = Value("продукты")
  val Appliances = Value("бытовая техника")
  val Services = Value("услуги")
  val Other = Value("прочее")
}

import ExpenseCategory.ExpenseCategory

case class Expense(id: Option[Long] = None,
                   name: String,
                   category: ExpenseCategory,
                   amount: Long,
                   parentId: Option[Long] = None)
