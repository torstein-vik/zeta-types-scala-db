package io.github.torsteinvik.zetatypes.db.query

import io.github.torsteinvik.zetatypes.db._

abstract sealed class Property[T] {
    def === (other : Property[T]) : Predicate = new EqualityPredicate[T](this, other)
}

abstract sealed class MFProperty[T] extends Property[T]
sealed case class ConstantProperty[T](value : T) extends Property[T]

trait Properties {
    import scala.language.implicitConversions
    implicit def liftProperty[T](t : T) : ConstantProperty[T] = ConstantProperty[T](t)
    
    case object mf extends MFProperty[MultiplicativeFunction]
    case object mflabel extends MFProperty[String]
}

object Property extends Properties
