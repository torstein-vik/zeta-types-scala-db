package io.github.torsteinvik.zetatypes.db

trait Property[T] {
    def get (x : MultiplicativeFunction) : T
}

trait Properties {
    import scala.language.implicitConversions
    implicit def liftProperty[T](t : T) : Property[T] = new Property[T]{def get(x : MultiplicativeFunction) = t}
    
    def mf : Property[MultiplicativeFunction] = new Property[MultiplicativeFunction]{def get(x : MultiplicativeFunction) = x}
}

object Property extends Properties
