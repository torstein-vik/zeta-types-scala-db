package io.github.torsteinvik.zetatypes.db

object Arithmetic {

    sealed abstract class ComplexNumber
    sealed abstract class Real extends ComplexNumber

    sealed case class Nat      (x : BigInt) extends Real {require(x > 0)}
    sealed case class Integer  (x : BigInt) extends Real
    sealed case class Floating (x : Float)  extends Real
    sealed case class Ratio    (num : Integer, den : Integer) extends Real {require(realToFloat(den) != 0)}
}
