package io.github.torsteinvik.zetatypes.db.query

import io.github.torsteinvik.zetatypes.db._

sealed abstract class Query[T] {
}


sealed class PropertyQuery[T] extends Query[T] {
    def ~[S](query : PropertyQuery[S]) : PropertyQuery[T ~ S] = new CombinedPropertyQuery[T, S](this, query)
}

final class SinglePropertyQuery[T](property : Property[T]) extends PropertyQuery[T]
final class CombinedPropertyQuery[T, S](query1 : PropertyQuery[T], query2 : PropertyQuery[S]) extends PropertyQuery[T ~ S]


