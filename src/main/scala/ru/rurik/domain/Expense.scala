package ru.rurik.domain

import ru.rurik.domain.ExpenseCategory.ExpenseCategory

case class Expense(id: Long,
                   name: String,
                   category: ExpenseCategory,
                   amount: Long,
                   parentId: Option[Long] = None,
                   userId: Option[Long] = None)
