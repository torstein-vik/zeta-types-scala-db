package io.github.torsteinvik.zetatypes.db

import query._

trait Database {
    def close() : Unit
    
    def batch(mfs : Seq[MultiplicativeFunction], batchid : Option[String] = None, time : Option[String] = None) : Unit
    def store(mf : MultiplicativeFunction, batchid : Option[String] = None, time : Option[String] = None) : Unit
    def get(mflabel : String) : MultiplicativeFunction
    def query[T](query : Query[T]) : QueryResult[T]
    
    def length : Int
    def getAll : Seq[MultiplicativeFunction]
}
