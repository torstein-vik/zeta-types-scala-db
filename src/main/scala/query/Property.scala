package io.github.torsteinvik.zetatypes.db.query

import io.github.torsteinvik.zetatypes.db._
import io.github.torsteinvik.zetatypes.db.Datatypes._


abstract sealed class Property[T] {
    def === (other : Property[T]) : Predicate = new EqualityPredicate[T](this, other)
    def !== (other : Property[T]) : Predicate = new EqualityPredicate[T](this, other).not
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

object Property extends Properties
