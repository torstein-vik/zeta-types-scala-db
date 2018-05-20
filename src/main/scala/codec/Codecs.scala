package io.github.torsteinvik.zetatypes.db.codec

import org.json4s._

trait Codecs {
    
    implicit object BigIntCodec extends Codec[BigInt] {
        def encode (x : BigInt) : JValue = if (x < BigInt(2).pow(32 - 1) && x > -BigInt(2).pow(32 - 1)) JInt(x) else {
            val mod = BigInt(2).pow(32 - 1)
            var rem = x
            val lst = collection.mutable.Buffer[Int]()
            
            while (rem != BigInt(0)) {
                val remm = rem % mod
                lst += remm.toInt
                rem = (rem - remm) / mod
            }
            
            return JArray(List(JString("base"),JInt(32 - 1),JArray(lst.map(JInt(_)).toList)))
        }
        def decode (x : JValue) : BigInt = x match {
            case JInt(num) => num
            case JArray(List(JString("base"), JInt(base), JArray(lst))) => {
                var sum = BigInt(0)
                
                for ((JInt(v), i) <- lst.zipWithIndex) {
                    sum = sum + v * BigInt(2).pow(base.toInt * i)
                }
                
                return sum
            }
            case _ => throw new CodecException("Found " + x + " expected BigInt (JInt)")
        }
    }
    
    implicit object DoubleCodec extends Codec[Double] {
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
    
    implicit def pairCodec[T1, T2](implicit codec1 : Codec[T1], codec2 : Codec[T2]) : Codec[(T1, T2)] = new PairCodec[T1, T2](codec1, codec2)
    class PairCodec[T1, T2] (codec1 : Codec[T1], codec2 : Codec[T2]) extends Codec[(T1, T2)] {
        def encode (x : (T1, T2)) : JValue = JArray(List(codec1.encode(x._1), codec2.encode(x._2)))
        def decode (x : JValue) : (T1, T2) = x match {
            case JArray(List(t1, t2)) => (codec1.decode(t1), codec2.decode(t2))
            case _ => throw new CodecException("Found " + x + " expected pair (JArray(x, y))")
        }
    }
    
    implicit def tripleCodec[T1, T2, T3](implicit codec1 : Codec[T1], codec2 : Codec[T2], codec3 : Codec[T3]) : Codec[(T1, T2, T3)] = new TripleCodec[T1, T2, T3](codec1, codec2, codec3)
    class TripleCodec[T1, T2, T3] (codec1 : Codec[T1], codec2 : Codec[T2], codec3 : Codec[T3]) extends Codec[(T1, T2, T3)] {
        def encode (x : (T1, T2, T3)) : JValue = JArray(List(codec1.encode(x._1), codec2.encode(x._2), codec3.encode(x._3)))
        def decode (x : JValue) : (T1, T2, T3) = x match {
            case JArray(List(t1, t2, t3)) => (codec1.decode(t1), codec2.decode(t2), codec3.decode(t3))
            case _ => throw new CodecException("Found " + x + " expected triple (JArray(x, y, z))")
        }
    }
    
}
