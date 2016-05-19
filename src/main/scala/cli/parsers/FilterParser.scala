package cli.parsers

import scala.util.parsing.combinator._

import dsl.Filter

object FilterParser extends JavaTokenParsers {

  def orFilter: Parser[String] = "," ~ ident ^^ { case _ ~ str => str }

  def filter: Parser[Filter] = ident ~ ":" ~ ident ~ (orFilter*) ^^ {
    case (attr ~ _ ~ value ~ orValueSeq) => Filter(attr, value :: orValueSeq)
  }

  def apply(input: String): Option[Filter] = parseAll(filter, input) match {
    case Success(res, _) => Some(res)
    case NoSuccess(_, _) => None
  }

}
