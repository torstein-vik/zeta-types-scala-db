package io.github.torsteinvik.zetatypes.db.codec

import scala.reflect.runtime.universe._

import org.json4s._

trait Codec[T] {
    def encode (x : T) : JValue
    def decode (x : JValue) : T
}

object Codec {
    def apply[T](implicit typetag : TypeTag[T]) : Codec[T] = Extraction[T](typetag).asInstanceOf[Codec[T]]
}
