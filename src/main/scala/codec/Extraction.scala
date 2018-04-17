package io.github.torsteinvik.zetatypes.db.codec

import scala.reflect.runtime.universe._

import org.json4s._

private[codec] object Extraction {
    def apply[T](implicit tt : TypeTag[T]) : Codec[_] = {
        
        typeOf[T] match {
            case t if t =:= typeOf[BigInt] => return BigIntCodec
            case t if t =:= typeOf[Double] => return DoubleCodec
            case t if t =:= typeOf[String] => return StringCodec
        }
        
        val typeSymbol : TypeSymbol = symbolOf[T]
        
        if (typeSymbol.isAbstract) throw new Exception("Can't create codec for abstract type " + typeSymbol.toString)
        
        throw new Exception("Not Yet Implemented")
    }
    
    private object BigIntCodec extends Codec[BigInt]{
        // TODO: Note not up to spec, weird [base, 32, [...]] thing not accounted for, seems uneeded
        def encode (x : BigInt) : JValue = JInt(x)
        def decode (x : JValue) : BigInt = x match {
            case JInt(num) => num
            case _ => throw new Exception("Found " + x + " expected BigInt (JInt)")
        }
    }
    
    private object DoubleCodec extends Codec[Double]{
        // TODO: Note not up to spec, spec uses floating numbers
        def encode (x : Double) : JValue = JDouble(x)
        def decode (x : JValue) : Double = x match {
            case JDouble(num) => num
            case _ => throw new Exception("Found " + x + " expected Double (JDouble)")
        }
    }
    
    private object StringCodec extends Codec[String]{
        def encode (x : String) : JValue = JString(x)
        def decode (x : JValue) : String = x match {
            case JString(str) => str
            case _ => throw new Exception("Found " + x + " expected String (JString)")
        }
    }
}

/*

Needs to handle:

* case classes
* Option
* String
* BigInt
* Double
* Record
* List (*)

*/
