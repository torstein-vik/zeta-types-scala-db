package io.github.torsteinvik.zetatypes.db

import query._

trait Database {
    def close() : Unit
    
    def batch(mfs : Seq[MultiplicativeFunction], batchid : String = null) : Unit
    def store(mf : MultiplicativeFunction) : Unit
    def get(mflabel : String) : MultiplicativeFunction
    def query[T](query : Query[T]) : T
    
    def length : Int
    def getAll : Seq[MultiplicativeFunction]
}
