package io.github.torsteinvik.zetatypes.db.query

import io.github.torsteinvik.zetatypes.db._

sealed abstract class Query[T] {
    final def where(f : Predicate) : Query[T] = FilteredQuery[T](this, f)
}

case class FilteredQuery[T](query : Query[T], predicate : Predicate) extends Query[T]

sealed abstract class PropertyQuery[T] extends Query[T] {
    final def ~[S](query : PropertyQuery[S]) : PropertyQuery[T ~ S] = CombinedPropertyQuery[T, S](this, query)
}

case class SinglePropertyQuery[T](property : Property[T]) extends PropertyQuery[T]
case class CombinedPropertyQuery[T, S](query1 : PropertyQuery[T], query2 : PropertyQuery[S]) extends PropertyQuery[T ~ S]

case class ~[+T, +S](t : T, s : S) 
