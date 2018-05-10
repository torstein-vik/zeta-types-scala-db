package io.github.torsteinvik.zetatypes.db.query

trait QueryPointer {
    def evalMFProperty[T](prop : MFProperty[T]) : T
}
