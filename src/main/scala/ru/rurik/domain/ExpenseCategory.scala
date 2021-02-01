package ru.rurik.domain

object ExpenseCategory extends Enumeration {
  type ExpenseCategory = Value
  val Food = Value("продукты")
  val Appliances = Value("бытовая техника")
  val Services = Value("услуги")
  val Other = Value("прочее")
}
