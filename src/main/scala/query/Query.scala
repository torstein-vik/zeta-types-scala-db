package io.github.torsteinvik.zetatypes.db.query

case class Query[T] (projection : Projection[T], filter : Predicate = TruePredicate) {
    lazy val requirements = new Requirements(projection.requires ++ filter.requires)
    
    final def where(f : Predicate) : Query[T] = copy(
        filter = if (filter == TruePredicate) f else filter and f
    )
}
