package io.github.torsteinvik.zetatypes.db.query

case class Query[T] (projection : Projection[T], filter : Predicate = TruePredicate, limit : Option[Int] = None) {
    lazy val requirements = new Requirements(projection.requires ++ filter.requires)
    
    final def where(f : Predicate) : Query[T] = copy(
        filter = if (filter == TruePredicate) f else filter and f
    )
    
    final def take(n : Int) : Query[T] = copy(
        limit = Some(math.min(n, limit.getOrElse(n)))
    )
    
}
