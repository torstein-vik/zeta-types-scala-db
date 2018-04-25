package io.github.torsteinvik.zetatypes.db

import Query._

sealed abstract class Query[T] {
    def map[S](f : T => S) : Query[S] = new MappedQuery[T, S](this, f)
    def ~[S](query : Query[S]) : Query[T ~ S] = new CombinedQuery[T, S](this, query)
}

final class MappedQuery[T, S](query : Query[T], map : T => S) extends Query[S]
final class CombinedQuery[T, S](query1 : Query[T], query2 : Query[S]) extends Query[T ~ S]

object Query {
    type ~[S, T] = (S, T)
    
    def mf : Query[MultiplicativeFunction] = ???
}
