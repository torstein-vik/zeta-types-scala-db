package io.github.torsteinvik.zetatypes.db

trait Property[T] {
    def get (x : MultiplicativeFunction) : T
}
