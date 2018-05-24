package io.github.torsteinvik.zetatypes.db.query

sealed abstract class Projection[T] (val requires : Set[MFProperty[_]]) 

object Projection {    
    case class PSingle[T](property : Property[T]) extends Projection[T](property.requires)
    
    import scala.language.implicitConversions
    implicit def apply[T](p : Property[T]) : Projection[T] = PSingle[T](p)
    implicit def asQuery[T](p : Projection[T]) : Query[T] = Query(p)
}
