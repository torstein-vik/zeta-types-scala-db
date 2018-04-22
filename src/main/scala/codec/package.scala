package io.github.torsteinvik.zetatypes.db

import org.json4s._

/** This pacakge provides codecs, which transform into and out of JSON
 *  @author Torstein Vik
 */
package object codec extends codec.Codecs { // TODO: remove extension, put into companion object
    def encode[T](x : T)(implicit encoder : Encoder[T]) : JValue = encoder.encode(x)
    def decode[T](x : JValue)(implicit decoder : Decoder[T]) : T = decoder.decode(x)
} 
