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
    
    implicit def tuple2[S, T] (tuple : (Property[S], Property[T])) = TupledProperties2(tuple._1, tuple._2)
    implicit def tuple3[S, T, U] (tuple : (Property[S], Property[T], Property[U])) = TupledProperties3(tuple._1, tuple._2, tuple._3)
    implicit def tuple4[S, T, U, V] (tuple : (Property[S], Property[T], Property[U], Property[V])) = TupledProperties4(tuple._1, tuple._2, tuple._3, tuple._4)
    implicit def tuple5[S, T, U, V, W] (tuple : (Property[S], Property[T], Property[U], Property[V], Property[W])) = TupledProperties5(tuple._1, tuple._2, tuple._3, tuple._4, tuple._5)
        
    implicit def propertyToQuery[S, T](s : S)(implicit f : S => Property[T]) : Query[T] = Query(projection = f(s))
}
