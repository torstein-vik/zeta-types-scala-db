package io.github.torsteinvik.zetatypes.db

import org.json4s._

/** This pacakge provides codecs, which transform into and out of JSON
 *  @author Torstein Vik
 */
package object codec { 
    def encode[T](x : T)(implicit codec : Codec[T]) : JValue = codec.encode(x)
    def decode[T](x : JValue)(implicit codec : Codec[T]) : T = codec.decode(x)
} 
