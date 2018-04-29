package io.github.torsteinvik.zetatypes.db.query

import scala.util.matching.Regex

abstract sealed class Predicate {
    final def and (other : Predicate) = AndPredicate(this, other)
    final def or (other : Predicate) = OrPredicate(this, other)
    final def not = NotPredicate(this)
    
    final def unary_! = not
    final def & (other : Predicate) = and(other)
    final def | (other : Predicate) = or(other)
}

sealed case class EqualityPredicate[T](prop1 : Property[T], prop2 : Property[T]) extends Predicate
sealed case class StringContainsPredicate(superstr : Property[String], substr : Property[String]) extends Predicate
sealed case class RegexPredicate(str : Property[String], regex : Regex) extends Predicate

sealed case class SeqContainsPredicate[T](seq : Property[Seq[T]], element : Property[T]) extends Predicate
sealed case class SeqHasPredicate[T](seq : Property[Seq[T]], pred : Property[T] => Predicate) extends Predicate
sealed case class SeqAllPredicate[T](seq : Property[Seq[T]], pred : Property[T] => Predicate) extends Predicate

sealed case class AndPredicate(pred1 : Predicate, pred2 : Predicate) extends Predicate
sealed case class OrPredicate(pred1 : Predicate, pred2 : Predicate) extends Predicate
sealed case class NotPredicate(pred : Predicate) extends Predicate
