package dsl

import PredicateExpr._

case class PredicateExpr(cmp: CmpOp, value: Float)

object PredicateExpr {

  object CmpOp extends Enumeration {
    val LT, LTE, EQ, GTE, GT = Value
  }
  type CmpOp = CmpOp.Value

}
