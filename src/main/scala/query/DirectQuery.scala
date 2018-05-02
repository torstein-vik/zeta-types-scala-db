package io.github.torsteinvik.zetatypes.db.query

import io.github.torsteinvik.zetatypes.db._

object DirectQuery {
    
    def query[T](q : Query[T])(mfs : Seq[MultiplicativeFunction]) : QueryResult[T] = new QueryResult(q match {
    })
    
    def evalProperty[T](p : Property[T], mf : MultiplicativeFunction) : T = p match {
    }
    
    def evalPredicate(p : Predicate, mf : MultiplicativeFunction) : Boolean = p match {
    }
    
    
    
}
