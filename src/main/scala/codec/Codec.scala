package io.github.torsteinvik.zetatypes.db.codec

import org.json4s._

trait Codec[T] {
    def encode (x : T) : JValue
    def decode (x : JValue) : T
}
trait Encoder[T] {
    def encode (x : T) : JValue 
}
trait Decoder[T] {
    def decode (x : JValue) : T 
}

case class CodecException(str : String) extends Exception(str)
