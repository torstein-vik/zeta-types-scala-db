package io.github.torsteinvik.zetatypes.db.query

case class Query[T] (projection : Projection[T], filter : Predicate = TruePredicate, limit : Option[Int] = None) {
    lazy val requirements = new Requirements(projection.requires ++ filter.requires)
    
    def where(f : Predicate) : Query[T] = copy(
        filter = if (filter == TruePredicate) f else filter and f
    )
    
    def take(n : Int) : Query[T] = copy(
        limit = Some(math.min(n, limit.getOrElse(n)))
    )
    
    def filter(f : Predicate) : Query[T] = where(f)
    def limit(n : Int) : Query[T] = take(n)
}
