package io.github.torsteinvik.zetatypes.db.query

import io.github.torsteinvik.zetatypes.db._

abstract sealed class Property[T] {
    def === (other : Property[T]) : Predicate = new EqualityPredicate[T](this, other)
}


trait Properties {
}

object Property extends Properties
