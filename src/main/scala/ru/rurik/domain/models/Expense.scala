package ru.rurik.domain.models

import ru.rurik.domain.models.ExpenseCategory.ExpenseCategory

case class Expense(id: Long,
                   name: String,
                   category: ExpenseCategory,
                   amount: Long,
                   parentId: Option[Long] = None,
                   userId: Option[Long] = None)
