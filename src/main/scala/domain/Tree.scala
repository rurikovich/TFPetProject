package ru.rurik.domain


import cats.{Eval, Foldable}


trait Tree[A] {

  def value: A

  def leafs(): Option[List[Tree[A]]]

}

object Tree {

  implicit def foldableForTree: Foldable[Tree] = new Foldable[Tree] {
    override def foldLeft[A, B](fa: Tree[A], b: B)(f: (B, A) => B): B = fa.leafs() match {
      case Some(treeList) =>
        val acc = f(b, fa.value)
        treeList.foldLeft(acc)(
          (b, tree) => foldLeft(tree, b)(f)
        )
      case None => f(b, fa.value)
    }

    override def foldRight[A, B](fa: Tree[A], lb: Eval[B])(f: (A, Eval[B]) => Eval[B]): Eval[B] = fa.leafs() match {
      case Some(treeList) =>
        val acc = f(fa.value, lb)
        treeList.foldRight(acc)(
          (tree, b) => foldRight(tree, b)(f)
        )
      case None => f(fa.value, lb)
    }
  }

}