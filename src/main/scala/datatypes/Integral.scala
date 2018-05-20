package io.github.torsteinvik.zetatypes.db.datatypes

import io.github.torsteinvik.zetatypes.db.codec._
import org.json4s._


sealed abstract class Integral (val value : BigInt) extends Real (ComplexNumberSubtypeLock) {
    def toInt = value.toInt
}

/** A natural number >= 0, realizing a [[Real]] */
case class Nat (x : BigInt) extends Integral(x) {require(x >= 0)}
/** A prime number, realizing a [[Real]] */
case class Prime (x : BigInt) extends Integral(x) 
/** A (big) integer, realizing a [[Real]] */
case class Integer (x : BigInt) extends Integral(x)

object Nat extends CodecContainer[Nat](
    {case Nat(x) => /*JObject(List(JField("nat",*/ encode[BigInt](x)/*)))*/}, 
    {
        case JObject(List(JField("nat", x))) => new Nat(decode[BigInt](x))
        case x => new Nat(decode[BigInt](x))
    }
){
    import scala.language.implicitConversions
    implicit def intToNat(x : Int) : Nat = Nat(x)
}

object Prime extends CodecContainer[Prime](
    {case Prime(x) => /*JObject(List(JField("prime",*/ encode[BigInt](x)/*)))*/}, 
    {
        case JObject(List(JField("prime", x))) => new Prime(decode[BigInt](x))
        case x => new Prime(decode[BigInt](x))
    }
) {
    def apply(x : BigInt) = {require(x.isProbablePrime(10), "Prime number not prime: " + x); new Prime(x)}
    import scala.language.implicitConversions
    implicit def intToPrime(x : Int) : Prime = Prime(x)
}
object Integer extends CodecContainer[Integer]({case Integer(x) => encode[BigInt](x)}, {case x => new Integer(decode[BigInt](x))}) {
    import scala.language.implicitConversions
    implicit def intToInteger(x : Int) : Integer = Integer(x)
}
