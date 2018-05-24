package io.github.torsteinvik.zetatypes.db.mongo

import io.github.torsteinvik.zetatypes.db.query._

import org.bson.conversions.Bson
import org.mongodb.scala.model.Projections._

object ProjectionAssembly {
    
    private sealed abstract class FieldProjection[T] {def field : Field[T] }
    private case class Field[T] (property : JSONProperty[T]) extends FieldProjection[T] { def field = this}
    private case class Slice[T] (field : Field[Seq[T]], skip : Int, amount : Int) extends FieldProjection[Seq[T]]
    
    private def union (f1 : FieldProjection[_], f2 : FieldProjection[_]) : FieldProjection[_] = {require(f1.field == f2.field); (f1, f2) match {
        case (Slice(f, skip1, amt1), Slice(_, skip2, amt2)) => {
            val skip = math.min(skip1, skip2)
            val amt = math.max(skip1 + amt1, skip2 + amt2) - skip
            Slice(f, skip, amt)
        }
        case (f : Field[_], _) => f
        case (_, f : Field[_]) => f
    }}
    
    import io.github.torsteinvik.zetatypes.db.dbmath._
    private def toProjection (property : MFProperty[_]) : FieldProjection[_] = property match {
        case property : JSONProperty[_] => Field(property)
        case bellcell(p, _) => Slice(Field(belltable), Primes.indexOf(p), 1)
        case bellrow(p) => Slice(Field(belltable), Primes.indexOf(p), 1)
        case bellsmalltable(ps, _) => Slice(Field(belltable), 0, ps)
    }
    
    private def toBsonProjection (p : FieldProjection[_]) : Bson = p match {
        case Field(JSONProperty(path)) => include(path.mkString("."))
        case Slice(Field(JSONProperty(path)), skip, amt) => slice(path.mkString("."), skip, amt)
    }
    
    def apply (requirements : Set[MFProperty[_]]) : Bson = {
        if (requirements.contains(mf)) return exclude()
        
        val bsonProjections = requirements.map(toProjection).groupBy(_.field).toSeq.map(_._2.reduce(union(_, _))).map(toBsonProjection _)
        
        fields(bsonProjections : _*)
    }
}
