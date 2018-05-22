package io.github.torsteinvik.zetatypes.db.query

import scala.util.matching.Regex

abstract sealed class Predicate (val requires : Set[MFProperty[_]]) {
    final def and (other : Predicate) = AndPredicate(this, other)
    final def or (other : Predicate) = OrPredicate(this, other)
    final def not = NotPredicate(this)
    
    final def unary_! = not
    final def & (other : Predicate) = and(other)
    final def | (other : Predicate) = or(other)
}

case object TruePredicate extends Predicate(Set())

case class EqualityPredicate[T](prop1 : Property[T], prop2 : Property[T]) extends Predicate(prop1.requires ++ prop2.requires)
case class StringContainsPredicate(superstr : Property[String], substr : Property[String]) extends Predicate(superstr.requires ++ substr.requires)
case class RegexPredicate(str : Property[String], regex : Regex) extends Predicate(str.requires)

case class SeqContainsPredicate[T](seq : Property[Seq[T]], element : Property[T]) extends Predicate(seq.requires ++ element.requires)
case class SeqHasPredicate[T](seq : Property[Seq[T]], pred : PropertyLambda[T]) extends Predicate(seq.requires ++ pred.output.requires)
case class SeqAllPredicate[T](seq : Property[Seq[T]], pred : PropertyLambda[T]) extends Predicate(seq.requires ++ pred.output.requires)

case class ExistsPredicate[T](opt : Property[Option[T]]) extends Predicate(opt.requires)

case class AndPredicate(pred1 : Predicate, pred2 : Predicate) extends Predicate(pred1.requires ++ pred2.requires)
case class OrPredicate(pred1 : Predicate, pred2 : Predicate) extends Predicate(pred1.requires ++ pred2.requires)
case class NotPredicate(pred : Predicate) extends Predicate(pred.requires)

case class BooleanPredicate(value : Property[Boolean]) extends Predicate(value.requires)

object Predicate {
    import scala.language.implicitConversions
    implicit def liftBooleanPredicate(value : Property[Boolean]) = BooleanPredicate(value)
    implicit def liftBooleanOptionPredicate(value : Property[Option[Boolean]]) = value.exists and value.get
}
