package io.github.torsteinvik.zetatypes.db.codec

import org.json4s._

import scala.annotation._

@implicitNotFound(msg = "Could not find a codec for type ${T}")
trait Codec[T] {
    def encode (x : T) : JValue 
    def decode (x : JValue) : T 
}

object Codec extends Codecs

class CodecContainer[T] (val encoder : T => JValue, val decoder : JValue => T) { outer => 
    implicit object codec extends Codec[T] {
        def encode(x : T) : JValue = outer.encoder(x)
        def decode(x : JValue) : T = outer.decoder(x)
    }
}

case class CodecException(str : String) extends Exception(str)
