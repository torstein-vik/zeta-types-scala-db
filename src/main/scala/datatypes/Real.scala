package io.github.torsteinvik.zetatypes.db.datatypes

import io.github.torsteinvik.zetatypes.db.codec._
import org.json4s._

import scala.util.Try

/** Abstract data type representing a real number, and realizing a [[ComplexNumber]]*/
abstract class Real (lock : ComplexNumberSubtypeLock) extends ComplexNumber (lock) {
    def re = this
    def im = Integer(0)
}

/** A floating point number, realizing a [[Real]] */
case class Floating (x : Double) extends Real (ComplexNumberSubtypeLock)
/** A ratio of integers, realizing a [[Real]] */
case class Ratio (num : Integer, den : Integer) extends Real (ComplexNumberSubtypeLock) {require(realToDouble(den) != 0)}

object Floating extends CodecContainer[Floating]({case Floating(x) => encode[Double](x)}, {case x => new Floating(decode[Double](x))})
object Ratio extends CodecContainer[Ratio]({case Ratio(x, y) => encode[(Integer, Integer)]((x, y))}, {case t => decode[(Integer, Integer)](t) match {case (x, y) => new Ratio(x, y)}})

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
