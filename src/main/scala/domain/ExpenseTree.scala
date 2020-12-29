package ru.rurik
package domain

case class ExpenseTree(value: Expense, leafs: Option[List[ExpenseTree]] = None) extends Tree[Expense]
