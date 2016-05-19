package dsl

import PredicateExpr._

case class PredicateExpr(op: CmpOp, value: Float)

object PredicateExpr {

  sealed trait CmpOp {
    def apply(fst: Float, snd: Float): Boolean
  }

  object LT extends CmpOp {
    def apply(fst: Float, snd: Float) = fst < snd
  }

  object LTE extends CmpOp {
    def apply(fst: Float, snd: Float) = fst <= snd
  }

  object EQ extends CmpOp {
    def apply(fst: Float, snd: Float) = fst == snd
  }

  object GTE extends CmpOp {
    def apply(fst: Float, snd: Float) = fst >= snd
  }

  object GT extends CmpOp {
    def apply(fst: Float, snd: Float) = fst > snd
  }

}
