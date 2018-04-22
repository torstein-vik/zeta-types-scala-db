package io.github.torsteinvik.zetatypes.db.codec

import org.json4s._

trait Encoder[T] {
    def encode (x : T) : JValue 
}
trait Decoder[T] {
    def decode (x : JValue) : T 
}

trait Codec[T] extends Encoder[T] with Decoder[T]

case class CodecException(str : String) extends Exception(str)
