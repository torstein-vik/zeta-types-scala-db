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
    
    implicit def optionCodec[T](implicit codec : Codec[T]) : Codec[Option[T]] = new OptionCodec[T](codec)
    class OptionCodec[T] (codec : Codec[T]) extends Codec[Option[T]] {
        def encode (x : Option[T]) : JValue = x match {
            case None => JNull
            case Some(u) => codec.encode(u)
        }
        def decode (x : JValue) : Option[T] = x match {
            case JNull => None
            case _ => Some(codec.decode(x))
        }
    }
    
    implicit def listCodec[T](implicit codec : Codec[T]) : Codec[List[T]] = new ListCodec[T](codec)
    class ListCodec[T] (codec : Codec[T]) extends Codec[List[T]] {
        def encode (x : List[T]) : JValue = JArray(x.map(codec.encode))
        def decode (x : JValue) : List[T] = x match {
            case JArray(ls) => ls.map(codec.decode)
            case _ => throw new CodecException("Found " + x + " expected array (JArray)")
        }
    }
    implicit def seqCodec[T](implicit codec : Codec[T]) : Codec[Seq[T]] = new SeqCodec[T](codec)
    class SeqCodec[T] (codec : Codec[T]) extends Codec[Seq[T]] {
        def encode (x : Seq[T]) : JValue = JArray(x.map(codec.encode).toList)
        def decode (x : JValue) : Seq[T] = x match {
            case JArray(ls) => ls.map(codec.decode)
            case _ => throw new CodecException("Found " + x + " expected array (JArray)")
        }
    }
    
    
}
