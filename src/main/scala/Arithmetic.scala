package io.github.torsteinvik.zetatypes.db

object Arithmetic {

    import scala.language.implicitConversions

    sealed abstract class ComplexNumber
    sealed abstract class Real extends ComplexNumber

    sealed case class Nat      (x : BigInt) extends Real {require(x > 0)}
    sealed case class Integer  (x : BigInt) extends Real
    sealed case class Floating (x : Float)  extends Real
    sealed case class Ratio    (num : Integer, den : Integer) extends Real {require(realToFloat(den) != 0)}

    sealed case class CartesianComplex (re : Real, im : Real) extends ComplexNumber
    sealed case class PolarComplex (abs : Real, unitarg : Real) extends ComplexNumber {require(abs >= 0 && unitarg >= 0 && unitarg < 1)}

    implicit def realToFloat(x : Real) : Float = x match {
        case Nat(x) => x.floatValue
        case Integer(x) => x.floatValue
        case Floating(x) => x
        case Ratio(Integer(x), Integer(y)) => x.floatValue / y.floatValue
    }

}
