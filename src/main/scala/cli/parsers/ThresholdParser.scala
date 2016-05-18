package cli.parsers

import scala.util.parsing.combinator._

import dsl.{Threshold, PredicateExpr}

object ThresholdParser extends JavaTokenParsers {

  def eq: Parser[PredicateExpr.CmpOp] = "=" ^^ (_ => PredicateExpr.CmpOp.EQ)

  def lt: Parser[PredicateExpr.CmpOp] = "<" ^^ (_ => PredicateExpr.CmpOp.LT)

  def gt: Parser[PredicateExpr.CmpOp] = ">" ^^ (_ => PredicateExpr.CmpOp.GT)

  def lte: Parser[PredicateExpr.CmpOp] = "<=" ^^ (_ => PredicateExpr.CmpOp.LTE)

  def gte: Parser[PredicateExpr.CmpOp] = ">=" ^^ (_ => PredicateExpr.CmpOp.GTE)

  def cmp: Parser[PredicateExpr.CmpOp] = lt | lte | eq | gt | gte

  def predicate: Parser[PredicateExpr] = cmp ~ floatingPointNumber ^^ {
    case (op ~ value) => PredicateExpr(op, value.toFloat)
  }

  def orPredicate: Parser[PredicateExpr] = "," ~ predicate ^^ { case _ ~ expr => expr }

  def threshold: Parser[Threshold] = stringLiteral ~ ":" ~ predicate ~ orPredicate ^^ {
    case (attr ~ _ ~ expr ~ orExprSeq) => Threshold(attr, Seq(expr))
  }

  def apply(input: String): Option[Threshold] = parseAll(threshold, input) match {
    case Success(res, _) => Some(res)
    case NoSuccess(_, _) => None
  }

}
