package io.github.torsteinvik.zetatypes.db.query

abstract sealed class Predicate {
    def and (other : Predicate) = new AndPredicate(this, other)
    def or (other : Predicate) = new OrPredicate(this, other)
}

sealed case class EqualityPredicate[T](prop1 : Property[T], prop2 : Property[T]) extends Predicate
sealed case class AndPredicate(pred1 : Predicate, pred2 : Predicate) extends Predicate
sealed case class OrPredicate(pred1 : Predicate, pred2 : Predicate) extends Predicate

