package io.github.torsteinvik.zetatypes.db.query

object QueryTools {
    
    def query[T](q : Query[T])(mfs : Seq[MFPropertyProvider]) : QueryResult[T] = aggregate(q)(new QueryResult(mfs.map(filterAndProjectOne(q)(_)).flatten))
    
    def aggregate[T](q : Query[T])(mfs : QueryResult[T]) : QueryResult[T] = {
        
        new QueryResult(mfs.take(q.limit.getOrElse(mfs.length)))
    }
    
    def filterAndProjectOne[T](q : Query[T])(mf : MFPropertyProvider) : Option[T] = {
        val eval = new StandardEvaluator(mf)
        
        if (eval(q.filter)) Some(eval(q.projection)) else None
    }
}
