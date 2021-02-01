package ru.rurik.domain.models

import ru.rurik.domain.models.common.Tree

case class ExpenseTree(value: Expense, leafs: Option[List[ExpenseTree]] = None) extends Tree[Expense]
