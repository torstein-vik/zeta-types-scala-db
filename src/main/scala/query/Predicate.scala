package io.github.torsteinvik.zetatypes.db.query

import scala.util.matching.Regex

abstract sealed class Predicate {
    def and (other : Predicate) = new AndPredicate(this, other)
    def or (other : Predicate) = new OrPredicate(this, other)
    def not = new NotPredicate(this)
    
    def unary_! = not
    def & (other : Predicate) = and(other)
    def | (other : Predicate) = or(other)
}

sealed case class EqualityPredicate[T](prop1 : Property[T], prop2 : Property[T]) extends Predicate
sealed case class StringContainsPredicate(superstr : Property[String], substr : Property[String]) extends Predicate
sealed case class RegexPredicate(str : Property[String], regex : Regex) extends Predicate

sealed case class AndPredicate(pred1 : Predicate, pred2 : Predicate) extends Predicate
sealed case class OrPredicate(pred1 : Predicate, pred2 : Predicate) extends Predicate
sealed case class NotPredicate(pred : Predicate) extends Predicate
