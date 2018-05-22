package io.github.torsteinvik.zetatypes.db.query

sealed abstract class Projection[T] (val requires : Set[MFProperty[_]]) {
    final def ~[S](query : Projection[S]) : Projection[T ~ S] = Projection.PCombined[T, S](this, query)
}

object Projection {    
    case class PSingle[T](property : Property[T]) extends Projection[T](property.requires)
    case class PCombined[T, S](proj1 : Projection[T], proj2 : Projection[S]) extends Projection[T ~ S](proj1.requires ++ proj2.requires)
    
    import scala.language.implicitConversions
    implicit def apply[T](p : Property[T]) : Projection[T] = PSingle[T](p)
    implicit def asQuery[T](p : Projection[T]) : Query[T] = Query(p)
}

case class ~[+T, +S](t : T, s : S) 
