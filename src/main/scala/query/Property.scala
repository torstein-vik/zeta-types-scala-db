package io.github.torsteinvik.zetatypes.db.query

import io.github.torsteinvik.zetatypes.db._
import io.github.torsteinvik.zetatypes.db.Datatypes._

import scala.util.matching.Regex

abstract sealed class Property[T] {
    final def === (other : Property[T]) : Predicate = EqualityPredicate[T](this, other)
    final def !== (other : Property[T]) : Predicate = EqualityPredicate[T](this, other).not
}

abstract sealed class MFProperty[T] extends Property[T]
case class ConstantProperty[T](value : T) extends Property[T]

trait Properties {
    import scala.language.implicitConversions
    implicit def liftProperty[S, T](s : S)(implicit f : S => T) : ConstantProperty[T] = ConstantProperty[T](f(s))
    
    case object mf extends MFProperty[MultiplicativeFunction]
    case object mflabel extends MFProperty[String]
    case object batchid extends MFProperty[String]
    case object name extends MFProperty[String]
    case object definition extends MFProperty[String]
    case object comments extends MFProperty[Seq[String]]
    case object keywords extends MFProperty[Seq[String]]
    
    case class mfvalue(n : Nat) extends Property[ComplexNumber]
    case class mfbell(p : Prime, e : Nat) extends Property[ComplexNumber]
}

object Property extends Properties {
    implicit final class StringProperty(prop : Property[String]) {
        def contains (contains : Property[String]) : Predicate = StringContainsPredicate(prop, contains)
        def matches (regex : Regex) : Predicate = RegexPredicate(prop, regex)
    }

    implicit final class SeqProperty[T](prop : Property[Seq[T]]) {
        def contains (contains : Property[T]) : Predicate = SeqContainsPredicate(prop, contains)
        def has (pred : Property[T] => Predicate) : Predicate = SeqHasPredicate(prop, pred)
        def all (pred : Property[T] => Predicate) : Predicate = SeqAllPredicate(prop, pred)
    }
}
