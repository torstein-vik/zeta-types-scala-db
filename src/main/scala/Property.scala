package io.github.torsteinvik.zetatypes.db

trait Property[T] {
    def get (x : MultiplicativeFunction) : T
}

trait Properties {
    import scala.language.implicitConversions
    
    def mf : Property[MultiplicativeFunction] = new Property[MultiplicativeFunction]{def get(x : MultiplicativeFunction) = x}
}

object Property extends Properties
