package dsl

case class Filter[T](attr: String, values: Seq[T])
