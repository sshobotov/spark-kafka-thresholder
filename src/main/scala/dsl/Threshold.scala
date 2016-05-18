package dsl

case class Threshold[T](attr: String, predicates: Seq[PredicateExpr[T]])
