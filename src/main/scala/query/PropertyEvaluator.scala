package io.github.torsteinvik.zetatypes.db.query

trait PropertyEvaluator {
    def apply[T] (p : Property[T]) : T
}
