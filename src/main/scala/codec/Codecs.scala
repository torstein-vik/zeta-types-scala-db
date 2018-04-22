package io.github.torsteinvik.zetatypes.db.codec

import org.json4s._

trait Codecs {
    
    implicit object BigIntCodec extends Codec[BigInt] {
        // TODO: Note not up to spec, weird [base, 32, [...]] thing not accounted for, seems uneeded
        def encode (x : BigInt) : JValue = JInt(x)
        def decode (x : JValue) : BigInt = x match {
            case JInt(num) => num
            case _ => throw new CodecException("Found " + x + " expected BigInt (JInt)")
        }
    }
    
    
}
