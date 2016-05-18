package cli.parsers

import scala.util.parsing.combinator._

import dsl.Filter

object FilterParser extends JavaTokenParsers {

  def orFilter: Parser[String] = "," ~ stringLiteral ^^ { case _ ~ str => str }

  def filter: Parser[Filter] = stringLiteral ~ ":" ~ stringLiteral ~ orFilter ^^ {
    case (attr ~ _ ~ value ~ orValueSeq) => Filter(attr, Seq(value))
  }

  def apply(input: String): Option[Filter] = parseAll(filter, input) match {
    case Success(res, _) => Some(res)
    case NoSuccess(_, _) => None
  }

}
