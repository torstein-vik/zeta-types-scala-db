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
    
    implicit object DoubleCodec extends Codec[Double] {
        // TODO: Note not up to spec, spec uses floating numbers
        def encode (x : Double) : JValue = JDouble(x)
        def decode (x : JValue) : Double = x match {
            case JDouble(num) => num
            case _ => throw new CodecException("Found " + x + " expected Double (JDouble)")
        }
    }
    
    implicit object StringCodec extends Codec[String] {
        def encode (x : String) : JValue = JString(x)
        def decode (x : JValue) : String = x match {
            case JString(str) => str
            case _ => throw new CodecException("Found " + x + " expected String (JString)")
        }
    }
    
    implicit object BooleanCodec extends Codec[Boolean] {
        def encode (x : Boolean) : JValue = JBool(x)
        def decode (x : JValue) : Boolean = x match {
            case JBool(bool) => bool
            case _ => throw new CodecException("Found " + x + " expected true or false (JBool)")
        }
    }
    
    
}
