package io.github.torsteinvik.zetatypes.db

trait Database {
    def batch(mfs : Seq[MultiplicativeFunction], batchid : String = null) : Unit
    def store(mf : MultiplicativeFunction) : Unit
    def get(mflabel : String) : MultiplicativeFunction
    def query[T](query : Query[T]) : T
}
