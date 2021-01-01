package ru.rurik
package domain

import domain.ExpenseCategory.ExpenseCategory

case class Expense(id: Long,
                   name: String,
                   category: ExpenseCategory,
                   amount: Long,
                   parentId: Option[Long] = None,
                   userId: Option[Long] = None)
