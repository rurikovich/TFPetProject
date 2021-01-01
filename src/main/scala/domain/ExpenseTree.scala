package ru.rurik
package domain

import common.Tree

case class ExpenseTree(value: Expense, leafs: Option[List[ExpenseTree]] = None) extends Tree[Expense]
