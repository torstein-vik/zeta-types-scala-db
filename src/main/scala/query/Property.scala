package io.github.torsteinvik.zetatypes.db.query

import io.github.torsteinvik.zetatypes.db.datatypes._

import scala.util.matching.Regex

private[query] sealed abstract class PropertySubtypeLock
private[query] case object PropertySubtypeLock extends PropertySubtypeLock

abstract class Property[T] (lock : PropertySubtypeLock) {
    require (lock == PropertySubtypeLock, "Subtyping Property is not allowed without the subtyping lock!")
    
    final type output = T
    
    def requires : Set[MFProperty[_]]
    
    final def === (other : Property[T]) : Predicate = EqualityPredicate[T](this, other)
    final def !== (other : Property[T]) : Predicate = EqualityPredicate[T](this, other).not
}

case class PropertyLambda[T](output : Predicate)
case class LambdaInputProperty[T]() extends Property[T](PropertySubtypeLock) {def requires = Set()}

object Property {
    import scala.language.implicitConversions
    implicit def liftProperty[S, T](s : S)(implicit f : S => T) : ConstantProperty[T] = ConstantProperty[T](f(s))
    implicit def liftPropertyLambda[T](f : Property[T] => Predicate) : PropertyLambda[T] = PropertyLambda[T](f(LambdaInputProperty[T]()))
    
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
    
        
    implicit def toProjection[S, T](s : S)(implicit f : S => Property[T]) : Projection[T] = Projection(f(s))
    implicit def toQuery[S, T](s : S)(implicit f : S => Property[T]) : Query[T] = Query(Projection(f(s)))
}
