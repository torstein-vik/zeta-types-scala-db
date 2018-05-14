package io.github.torsteinvik.zetatypes.db.query

import io.github.torsteinvik.zetatypes.db._
import io.github.torsteinvik.zetatypes.db.dbmath._
import io.github.torsteinvik.zetatypes.db.Datatypes._

import scala.util.matching.Regex

abstract sealed class Property[T] {
    final type output = T
    
    def requires : Set[MFProperty[_]]
    
    final def === (other : Property[T]) : Predicate = EqualityPredicate[T](this, other)
    final def !== (other : Property[T]) : Predicate = EqualityPredicate[T](this, other).not
}

abstract sealed class MFProperty[T] extends Property[T] {def requires = Set(this)}
abstract sealed class CompoundProperty[T](requirements : Set[MFProperty[_]]) extends Property[T] {def requires = requirements} 

case class ConstantProperty[T](value : T) extends CompoundProperty[T](Set())
case class GetProperty[T](inner : Property[Option[T]]) extends CompoundProperty[T](inner.requires)
case class ApplyProperty[T](inner : Property[Record[T]], name : String) extends CompoundProperty[Option[T]](inner.requires)
case class TupleFirstProperty[T, S](inner : Property[(T, S)]) extends CompoundProperty[T](inner.requires)
case class TupleSecondProperty[T, S](inner : Property[(T, S)]) extends CompoundProperty[S](inner.requires)

case class PropertyLambda[T](output : Predicate)
case class LambdaInputProperty[T]() extends CompoundProperty[T](Set())

trait Properties {
    import scala.language.implicitConversions
    implicit def liftProperty[S, T](s : S)(implicit f : S => T) : ConstantProperty[T] = ConstantProperty[T](f(s))
    implicit def liftPropertyLambda[T](f : Property[T] => Predicate) : PropertyLambda[T] = PropertyLambda[T](f(LambdaInputProperty[T]()))
    
    case object mf extends MFProperty[MultiplicativeFunction]
    case object mflabel extends MFProperty[String]
    case object batchid extends MFProperty[Option[String]]
    case object name extends MFProperty[String]
    case object definition extends MFProperty[String]
    case object comments extends MFProperty[Seq[String]]
    case object properties extends MFProperty[Record[Boolean]]
    case class bellcell(p : Prime, e : Nat) extends MFProperty[Option[ComplexNumber]]
    case class bellrow(p : Prime) extends MFProperty[Option[Seq[ComplexNumber]]]
    case class bellsmalltable(ps : Int = 10, es : Int = 15) extends MFProperty[Seq[(Prime, Seq[ComplexNumber])]]
    case object belltable extends MFProperty[Seq[(Prime, Seq[ComplexNumber])]]
    
    case class pretty(ps : Int = 10, es : Int = 15) extends CompoundProperty[String](Set(bellsmalltable(ps, es), mflabel, name, definition))
    case class mfvalue(n : Nat) extends CompoundProperty[Option[ComplexNumber]](Factor(n).toSet.map(bellcell.tupled)) {
        val factors : Set[bellcell] = requires.asInstanceOf[Set[bellcell]]
    }
}

object Property extends Properties {
    implicit final class StringProperty(prop : Property[String]) {
        def contains (contains : Property[String]) : Predicate = StringContainsPredicate(prop, contains)
        def matches (regex : Regex) : Predicate = RegexPredicate(prop, regex)
    }
    
    implicit final class OptionProperty[T](prop : Property[Option[T]]) {
        def get = GetProperty(prop)
        
        def exists = ExistsPredicate(prop)
        final def ==? (other : Property[T]) : Predicate = exists and EqualityPredicate[T](get, other)
        final def !=? (other : Property[T]) : Predicate = exists and EqualityPredicate[T](get, other).not
    }

    implicit final class SeqProperty[T](prop : Property[Seq[T]]) {
        def contains (contains : Property[T]) : Predicate = SeqContainsPredicate(prop, contains)
        def has (pred : Property[T] => Predicate) : Predicate = SeqHasPredicate(prop, pred)
        def all (pred : Property[T] => Predicate) : Predicate = SeqAllPredicate(prop, pred)
    }

    implicit final class RecordProperty[T](prop : Property[Record[T]]) {
        def apply (name : String) : Property[Option[T]] = ApplyProperty(prop, name)
    }
    
    implicit final class TupleProperty[T, S](prop : Property[(T, S)]) {
        def _1 : Property[T] = TupleFirstProperty(prop)
        def _2 : Property[S] = TupleSecondProperty(prop)
    }
    
    import scala.language.implicitConversions
    implicit def propertyAsQuery[T](p : Property[T]) : PropertyQuery[T] = new SinglePropertyQuery[T](p)
}
