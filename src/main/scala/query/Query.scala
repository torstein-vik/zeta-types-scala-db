package io.github.torsteinvik.zetatypes.db.query

import io.github.torsteinvik.zetatypes.db._

sealed abstract class Query[T] (val requires : Set[MFProperty[_]]) {
    lazy val requirements = new Requirements(requires)
    
    final def where(f : Predicate) : Query[T] = FilteredQuery[T](this, f)
}

case class FilteredQuery[T](query : Query[T], predicate : Predicate) extends Query[T](query.requires ++ predicate.requires)

sealed abstract class PropertyQuery[T](requires : Set[MFProperty[_]]) extends Query[T](requires) {
    final def ~[S](query : PropertyQuery[S]) : PropertyQuery[T ~ S] = CombinedPropertyQuery[T, S](this, query)
}

case class SinglePropertyQuery[T](property : Property[T]) extends PropertyQuery[T](property.requires)
case class CombinedPropertyQuery[T, S](query1 : PropertyQuery[T], query2 : PropertyQuery[S]) extends PropertyQuery[T ~ S](query1.requires ++ query2.requires)

case class ~[+T, +S](t : T, s : S) 
