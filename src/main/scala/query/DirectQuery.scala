package io.github.torsteinvik.zetatypes.db.query

import io.github.torsteinvik.zetatypes.db._

object DirectQuery {
    
    def query[T](q : Query[T])(mfs : Seq[MultiplicativeFunction]) : QueryResult[T] = new QueryResult(q match {
    })
    
    def evalProperty[T](p : Property[T], mf : MultiplicativeFunction) : T = p match {
        case ConstantProperty(x) => x
        case GetProperty(x) => evalProperty[Option[T]](x, mf) match {
            case Some(y) => y
            case None => throw new Exception("Value assumed to exist in q did not exist!")
        }
        case p : MFProperty[T] => p match {
            case Property.mf => mf
            case Property.mflabel => mf.mflabel
            case Property.batchid => mf.metadata.batchId
            case Property.name => mf.metadata.descriptiveName
            case Property.belltable => mf.bellTableText
            case Property.definition => mf.metadata.verbalDefinition
            case Property.comments => mf.metadata.comments
            case Property.properties => mf.properties.entries.collect{ case (property, true) => property }
            
        }
    }
    
    def evalPredicate(p : Predicate, mf : MultiplicativeFunction) : Boolean = p match {
    }
    
    
    
}
