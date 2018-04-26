package io.github.torsteinvik.zetatypes.db

trait Database {
    def store(mf : MultiplicativeFunction) : String
    def get(mflabel : String) : MultiplicativeFunction
    def query[T](query : Query[T]) : T
}
