package io.github.torsteinvik.zetatypes.db

sealed abstract class Query[T] {
    def map[S](f : T => S) : Query[S] = new MappedQuery[T, S](this, f)
}

final class MappedQuery[T, S](query : Query[T], map : T => S) extends Query[S]

object Query {
}
