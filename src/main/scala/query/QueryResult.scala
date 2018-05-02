package io.github.torsteinvik.zetatypes.db.query

class QueryResult[T] (data : Seq[T]) extends Seq[T]{
    def iterator = data.iterator
    def apply(idx : Int) = data(idx)
    def length = data.length
    
    def print() = {
        println("Length of query results: " + length)
        foreach {x => 
            println(x)
            println()
            println()
        }
    }
}
