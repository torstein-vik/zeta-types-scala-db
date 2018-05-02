package io.github.torsteinvik.zetatypes.db.query

import io.github.torsteinvik.zetatypes.db._

object DirectQuery {
    
    def query[T](q : Query[T])(mfs : Seq[MultiplicativeFunction]) : QueryResult[T] = new QueryResult(q match {
    })
    
    
}
