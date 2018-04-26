package io.github.torsteinvik.zetatypes.db.query

abstract sealed class Predicate {
}

sealed case class EqualityPredicate[T](prop1 : Property[T], prop2 : Property[T]) extends Predicate
