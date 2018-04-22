package io.github.torsteinvik.zetatypes.db.codec

import org.json4s._

import scala.annotation._


@implicitNotFound(msg = "Could not find an encoder for type ${T}")
trait Encoder[T] {
    def encode (x : T) : JValue 
}

@implicitNotFound(msg = "Could not find a decoder for type ${T}")
trait Decoder[T] {
    def decode (x : JValue) : T 
}

@implicitNotFound(msg = "Could not find a codec for type ${T}")
trait Codec[T] extends Encoder[T] with Decoder[T]


class CodecContainer[T] (val encoder : T => JValue, val decoder : JValue => T) { outer => 
    implicit object codec extends Codec[T] {
        def encode(x : T) : JValue = outer.encoder(x)
        def decode(x : JValue) : T = outer.decoder(x)
    }
}

case class CodecException(str : String) extends Exception(str)
