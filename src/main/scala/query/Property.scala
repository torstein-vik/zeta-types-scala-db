package io.github.torsteinvik.zetatypes.db.query

import io.github.torsteinvik.zetatypes.db._
import io.github.torsteinvik.zetatypes.db.Datatypes._

import scala.util.matching.Regex

import scala.annotation.tailrec

abstract sealed class Property[T] {
    def requires : Set[MFProperty[_]]
    
    final def === (other : Property[T]) : Predicate = EqualityPredicate[T](this, other)
    final def !== (other : Property[T]) : Predicate = EqualityPredicate[T](this, other).not
}

case class ConstantProperty[T](value : T) extends Property[T]
case class GetProperty[T](inner : Property[Option[T]]) extends Property[T]
abstract sealed class MFProperty[T] extends Property[T] {def requires = Set(this)}
abstract sealed class CompoundProperty[T](requirements : Set[MFProperty[_]]) extends Property[T] {def requires = requirements} 

case class PropertyLambda[T](output : Predicate)
case class LambdaInputProperty[T]() extends Property[T]

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
    
    case class belltable(ps : Int = 10, es : Int = 15) extends MFProperty[String]
    
    case class mfvalue(n : Nat) extends MFProperty[Option[ComplexNumber]] {
        val factors = factor(n)
        
        //TODO: unify with other primes and naturals stream
        //credit: https://gist.github.com/ramn/8378315
        private lazy val primes: Stream[Int] = 2 #:: Stream.from(3).filter { n => !primes.takeWhile(_ <= math.sqrt(n)).exists(n % _ == 0) }
        //credit: https://stackoverflow.com/questions/8566532/scala-streams-and-their-memory-usage
        private lazy val naturals: Stream[Int] = Stream.cons(0, naturals.map{_ + 1})
        
        // TODO: move this elsewhere...
        private def factor(nat : Nat) : Seq[(Prime, Nat)] = {
            @tailrec
            def factor_(n : BigInt, curindex : Int, seq : Seq[(Int, Int)]) : Seq[(Int, Int)] = {
                if (n == BigInt(1)) return seq
                val (p : Int, i : Int) = primes.zipWithIndex.drop(curindex).find{n % _._1 == BigInt(0)}.get
                val e : Int = naturals.find(e => n % BigInt(p).pow(e) > 0).get - 1
                factor_(n / BigInt(p).pow(e), i + 1, (p, e) +: seq)
            }
            
            factor_(nat.x, 0, Seq()).map{case (p, e) => Prime(p) -> Nat(e)}
        }
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
}
