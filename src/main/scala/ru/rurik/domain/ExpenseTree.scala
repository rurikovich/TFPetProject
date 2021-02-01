package ru.rurik.domain

import ru.rurik.common.Tree

case class ExpenseTree(value: Expense, leafs: Option[List[ExpenseTree]] = None) extends Tree[Expense]
