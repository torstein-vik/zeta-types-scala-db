package io.github.torsteinvik.zetatypes.db

import codec._
import org.json4s._

import scala.util.Try

package Datatypes {
    
    /** Abstract data type representing a complex number*/
    sealed abstract class ComplexNumber
    /** Abstract data type representing a real number, and realizing a [[ComplexNumber]]*/
    sealed abstract class Real extends ComplexNumber

    /** A natural number >= 0, realizing a [[Real]] */
    sealed case class Nat (x : BigInt) extends Real {require(x >= 0)}
    /** A prime number, realizing a [[Real]] */
    sealed case class Prime (x : BigInt) extends Real {require(x.isProbablePrime(10))}
    /** A (big) integer, realizing a [[Real]] */
    sealed case class Integer (x : BigInt) extends Real
    /** A floating point number, realizing a [[Real]] */
    sealed case class Floating (x : Double) extends Real
    /** A ratio of integers, realizing a [[Real]] */
    sealed case class Ratio (num : Integer, den : Integer) extends Real {require(realToDouble(den) != 0)}

    /** A pair of [[Real]] numbers, representing a real and imaginary part of a [[ComplexNumber]] */
    sealed case class CartesianComplex (re : Real, im : Real) extends ComplexNumber
    /** A pair of [[Real]] numbers, representing the absolute value and argument (normalized to [0, 1[) of a [[ComplexNumber]] */
    sealed case class PolarComplex (abs : Real, unitarg : Real) extends ComplexNumber {require(abs >= 0 && unitarg >= 0 && unitarg < 1)}
    
    /** A polynomial with coefficients in the [[ComplexNumber]]s, stored sparsely */
    sealed case class ComplexPolynomial (coeffs : (ComplexNumber, Nat)*) 
    
    /** A Hybrid set of elements in the input type */
    sealed case class HybridSet[A] (multiplicities : (A, Integer)*)

    /** A JSON object, each value of specified type */
    sealed case class Record[T] (entries : (String, T)*)

    object Nat extends CodecContainer[Nat]({case Nat(x) => JObject(List(JField("nat", encode[BigInt](x))))}, {case JObject(List(JField("nat", x))) => new Nat(decode[BigInt](x))})
    object Prime extends CodecContainer[Prime]({case Prime(x) => JObject(List(JField("prime", encode[BigInt](x))))}, {case JObject(List(JField("prime", x))) => new Prime(decode[BigInt](x))})
    object Integer extends CodecContainer[Integer]({case Integer(x) => encode[BigInt](x)}, {case x => new Integer(decode[BigInt](x))})
    
    object Floating extends CodecContainer[Floating]({case Floating(x) => encode[Double](x)}, {case x => new Floating(decode[Double](x))})
    object Ratio extends CodecContainer[Ratio]({case Ratio(x, y) => encode[(Integer, Integer)]((x, y))}, {case t => decode[(Integer, Integer)](t) match {case (x, y) => new Ratio(x, y)}})
    
    object CartesianComplex extends CodecContainer[CartesianComplex](
        {case CartesianComplex(re, im) => new JObject(List(JField("re", encode[Real](re)), JField("im", encode[Real](im))))},
        {
            case JObject(List(JField("re", re), JField("im", im))) => new CartesianComplex(decode[Real](re), decode[Real](im)) 
            case x => throw new CodecException("Found " + x + " expected {\"re\": ..., \"im\": ...} (JObject(JField(\"re\", x), JField(\"im\", y)))")
        }
    )
    
    object PolarComplex extends CodecContainer[PolarComplex](
        {case PolarComplex(abs, unitarg) => new JObject(List(JField("abs", encode[Real](abs)), JField("unitarg", encode[Real](unitarg))))},
        {
            case JObject(List(JField("abs", abs), JField("unitarg", unitarg))) => new PolarComplex(decode[Real](abs), decode[Real](unitarg)) 
            case x => throw new CodecException("Found " + x + " expected {\"abs\": ..., \"unitarg\": ...} (JObject(JField(\"abs\", x), JField(\"unitarg\", y)))")
        }
    )
    
    object Real extends CodecContainer[Real](
        encoder = (x : Real) => x match { 
            case x : Nat => encode[Nat](x)
            case x : Prime => encode[Prime](x)
            case x : Integer => encode[Integer](x)
            case x : Floating => encode[Floating](x)
            case x : Ratio => encode[Ratio](x)
            case _ => throw new CodecException("Real number not in spec")
        },
        decoder = (x : JValue) => {
            (Try(decode[Nat](x)) getOrElse
            (Try(decode[Prime](x)) getOrElse
            (Try(decode[Integer](x)) getOrElse
            (Try(decode[Floating](x)) getOrElse
            (Try(decode[Ratio](x)) getOrElse
            {throw new CodecException("Could not parse Real number from " + x.toString) })))))
        }
    )
    
}

/** Provides data-types used in JSON-schema */
package object Datatypes extends LowerPriorityImplicits {

    import scala.language.implicitConversions

    /** Implicit conversion of any [[Real]] to a scala floting point number */
    implicit def realToDouble(x : Real) : Double = x match {
        case Prime(x) => x.doubleValue
        case Nat(x) => x.doubleValue
        case Integer(x) => x.doubleValue
        case Floating(x) => x
        case Ratio(Integer(x), Integer(y)) => x.doubleValue / y.doubleValue
    }
    
    implicit def floatToReal(x : Float) : Real = Floating(x.doubleValue)
    implicit def doubleToReal(x : Double) : Real = Floating(x)
    implicit def intToReal(x : Int) : Real = Integer(x)
    
    type PrimeLogSymbol = HybridSet[(Real, Real)]
    
}

trait LowerPriorityImplicits {
    import Datatypes._
    import scala.language.implicitConversions
    
    implicit def intToInteger(x : Int) : Integer = Integer(x)
    implicit def intToPrime(x : Int) : Prime = Prime(x)
    implicit def intToNat(x : Int) : Nat = Nat(x)
}
