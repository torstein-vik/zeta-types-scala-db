package io.github.torsteinvik.zetatypes.db.datatypes

import io.github.torsteinvik.zetatypes.db.codec._
import org.json4s._

import scala.util.Try

private[datatypes] sealed abstract class ComplexNumberSubtypeLock
private[datatypes] case object ComplexNumberSubtypeLock extends ComplexNumberSubtypeLock

/** Abstract data type representing a complex number*/
abstract class ComplexNumber (lock : ComplexNumberSubtypeLock) {
    require (lock == ComplexNumberSubtypeLock, "Subtyping ComplexNumber is not allowed without the subtyping lock!")
    
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

/** A pair of [[Real]] numbers, representing a real and imaginary part of a [[ComplexNumber]] */
case class CartesianComplex (re : Real, im : Real) extends ComplexNumber (ComplexNumberSubtypeLock)
/** A pair of [[Real]] numbers, representing the absolute value and argument (normalized to [0, 1[) of a [[ComplexNumber]] */
case class PolarComplex (abs : Real, unitarg : Real) extends ComplexNumber (ComplexNumberSubtypeLock) {
    require(abs >= 0 && unitarg >= 0 && unitarg < 1)
    def re = Floating(abs * math.cos(unitarg * 2 * math.Pi))
    def im = Floating(abs * math.sin(unitarg * 2 * math.Pi))
    
}

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
) {
    import scala.language.implicitConversions
    implicit def floatToComplex(x : Float) : ComplexNumber = Floating(x.doubleValue)
    implicit def doubleToComplex(x : Double) : ComplexNumber = Floating(x)
    implicit def intToComplex(x : Int) : ComplexNumber = Integer(x)
}
