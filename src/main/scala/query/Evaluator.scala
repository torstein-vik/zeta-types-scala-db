package io.github.torsteinvik.zetatypes.db.query

trait Evaluator {
    def apply[T] (p : Property[T]) : T
}
