package dsl

//import PredicateExpr._

case class PredicateExpr[T](cmp: Ordering[T], value: T)

//object PredicateExpr {
//
//  object CmpOperator extends Enumeration {
//    val LT, LTE, EQ, GTE, GT = Value
//  }
//  type CmpOperator = CmpOperator.Value
//
//}
