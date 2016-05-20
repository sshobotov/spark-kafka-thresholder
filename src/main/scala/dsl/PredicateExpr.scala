package dsl

import PredicateExpr._

case class PredicateExpr(op: CmpOp, value: Float)

object PredicateExpr {

  sealed trait CmpOp extends Serializable

  object LT extends CmpOp

  object LTE extends CmpOp

  object EQ extends CmpOp

  object GTE extends CmpOp

  object GT extends CmpOp

  implicit class PredicateExprOps(val expr: PredicateExpr) extends AnyVal {

    def evalFor(that: Float) = expr.op match {
      case LT => that < expr.value
      case LTE => that <= expr.value
      case EQ => that == expr.value
      case GTE => that >= expr.value
      case GT => that > expr.value
    }

  }

}
