package io.github.torsteinvik.zetatypes.db.datatypes

import io.github.torsteinvik.zetatypes.db._
import io.github.torsteinvik.zetatypes.db.codec._
import org.json4s._

import scala.util.Try

/** Abstract data type representing a complex number*/
sealed abstract class ComplexNumber {
    def re : Real
    def im : Real
    
    def pretty : String = this match {
        case CartesianComplex(re, im) => re.pretty + " " + im.pretty + "i"
        case PolarComplex(abs, unitarg) => abs.pretty + " @ 2pi*" + unitarg.pretty
        case Nat(x) => x.toString
        case Prime(x) => x.toString
        case Integer(x) => x.toString
        case Floating(x) => x.toString
        case Ratio(x, y) => x.pretty + "/" + y.pretty
    }
    
    override def equals (other : Any) = other match {
        case other : ComplexNumber => (re : Double) == (other.re : Double) && (im : Double) == (other.im : Double)
        case _ => false
    }
}
/** Abstract data type representing a real number, and realizing a [[ComplexNumber]]*/
sealed abstract class Real extends ComplexNumber {
    def re = this
    def im = Integer(0)
}

sealed abstract class Integral (val value : BigInt) extends Real {
    def toInt = value.toInt
}

/** A natural number >= 0, realizing a [[Real]] */
case class Nat (x : BigInt) extends Integral(x) {require(x >= 0)}
/** A prime number, realizing a [[Real]] */
case class Prime (x : BigInt) extends Integral(x) 
/** A (big) integer, realizing a [[Real]] */
case class Integer (x : BigInt) extends Integral(x)
/** A floating point number, realizing a [[Real]] */
case class Floating (x : Double) extends Real 
/** A ratio of integers, realizing a [[Real]] */
case class Ratio (num : Integer, den : Integer) extends Real {require(realToDouble(den) != 0)}

/** A pair of [[Real]] numbers, representing a real and imaginary part of a [[ComplexNumber]] */
case class CartesianComplex (re : Real, im : Real) extends ComplexNumber
/** A pair of [[Real]] numbers, representing the absolute value and argument (normalized to [0, 1[) of a [[ComplexNumber]] */
case class PolarComplex (abs : Real, unitarg : Real) extends ComplexNumber {
    require(abs >= 0 && unitarg >= 0 && unitarg < 1)
    def re = Floating(abs * math.cos(unitarg * 2 * math.Pi))
    def im = Floating(abs * math.sin(unitarg * 2 * math.Pi))
    
}

/** A polynomial with coefficients in the [[ComplexNumber]]s, stored sparsely */
case class ComplexPolynomial (coeffs : (ComplexNumber, Nat)*) 

/** A Hybrid set of elements in the input type */
case class HybridSet[A] (multiplicities : (A, Integer)*)

/** A JSON object, each value of specified type */
case class Record[T] (entries : (String, T)*)

object Nat extends CodecContainer[Nat](
    {case Nat(x) => /*JObject(List(JField("nat",*/ encode[BigInt](x)/*)))*/}, 
    {
        case JObject(List(JField("nat", x))) => new Nat(decode[BigInt](x))
        case x => new Nat(decode[BigInt](x))
    }
 )
object Prime extends CodecContainer[Prime](
    {case Prime(x) => /*JObject(List(JField("prime",*/ encode[BigInt](x)/*)))*/}, 
    {
        case JObject(List(JField("prime", x))) => new Prime(decode[BigInt](x))
        case x => new Prime(decode[BigInt](x))
    }
) {
    def apply(x : BigInt) = {require(x.isProbablePrime(10), "Prime number not prime: " + x); new Prime(x)}
}
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
        case x : Integer => encode[Integer](x)
        case x : Floating => encode[Floating](x)
        case x : Ratio => encode[Ratio](x)
        case x : Nat => encode[Nat](x)
        case x : Prime => encode[Prime](x)
        case _ => throw new CodecException("Real number not in spec")
    },
    decoder = (x : JValue) => {
        (Try(decode[Integer](x)) getOrElse
        (Try(decode[Floating](x)) getOrElse
        (Try(decode[Ratio](x)) getOrElse
        (Try(decode[Nat](x)) getOrElse
        (Try(decode[Prime](x)) getOrElse
        {throw new CodecException("Could not parse Real number from " + x.toString) })))))
    }
)

object ComplexNumber extends CodecContainer[ComplexNumber](
    encoder = (x : ComplexNumber) => x match { 
        case x : Real => encode[Real](x)
        case x : CartesianComplex => encode[CartesianComplex](x)
        case x : PolarComplex => encode[PolarComplex](x)
        case _ => throw new CodecException("ComplexNumber not in spec")
    },
    decoder = (x : JValue) => {
        (Try(decode[Real](x)) getOrElse
        (Try(decode[CartesianComplex](x)) getOrElse
        (Try(decode[PolarComplex](x)) getOrElse
        {throw new CodecException("Could not parse ComplexNumber from " + x.toString) })))
    }
)

object ComplexPolynomial extends CodecContainer[ComplexPolynomial](
    { case ComplexPolynomial(x @ _*) => JObject(List(JField("monomials", encode[List[(ComplexNumber, Nat)]](x.toList))))},
    { case JObject(List(JField("monomials", x))) => new ComplexPolynomial(decode[List[(ComplexNumber, Nat)]](x) : _*)}
)

object HybridSet {
    implicit def hybridSetCodec[A](implicit codecA : Codec[A]) : Codec[HybridSet[A]] = new Codec[HybridSet[A]] {
        def encode (x : HybridSet[A]) = codec.encode[List[(A, Integer)]](x.multiplicities.toList)
        def decode (x : JValue) = HybridSet[A](codec.decode[List[(A, Integer)]](x) : _*)
    }
}

object Record {
    implicit def recordCodec[A](implicit codec : Codec[A]) : Codec[Record[A]] = new Codec[Record[A]] {
        def encode (x : Record[A]) = JObject(x.entries.map{case (field, a) => JField(field, codec.encode(a))}.toList)
        def decode (x : JValue) = x match {
            case JObject(ls) => Record[A](ls.map{case JField(field, a) => (field, codec.decode(a))} : _*)
            case _ => throw new CodecException("Found " + x + " expected object (JObject)")
        }
    }
}


