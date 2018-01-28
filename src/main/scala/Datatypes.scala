package io.github.torsteinvik.zetatypes.db

package Datatypes {
    
    /** Abstract data type representing a complex number*/
    sealed abstract class ComplexNumber
    /** Abstract data type representing a real number, and realizing a [[ComplexNumber]]*/
    sealed abstract class Real extends ComplexNumber

    /** A natural number >= 0, realizing a [[Real]] */
    sealed case class Nat (x : BigInt) extends Real {require(x >= 0)}
    /** A (big) integer, realizing a [[Real]] */
    sealed case class Integer (x : BigInt) extends Real
    /** A floating point number, realizing a [[Real]] */
    sealed case class Floating (x : Float) extends Real
    /** A ratio of integers, realizing a [[Real]] */
    sealed case class Ratio (num : Integer, den : Integer) extends Real {require(realToFloat(den) != 0)}

    /** A pair of [[Real]] numbers, representing a real and imaginary part of a [[ComplexNumber]] */
    sealed case class CartesianComplex (re : Real, im : Real) extends ComplexNumber
    /** A pair of [[Real]] numbers, representing the absolute value and argument (normalized to [0, 1[) of a [[ComplexNumber]] */
    sealed case class PolarComplex (abs : Real, unitarg : Real) extends ComplexNumber {require(abs >= 0 && unitarg >= 0 && unitarg < 1)}
    
    /** A polynomial with coefficients in the [[ComplexNumber]]s, stored sparsely */
    sealed case class ComplexPolynomial (coeffs : (ComplexNumber, Nat)*) 
    
    /** A Hybrid set of elements in the input type */
    sealed case class HybridSet[A] (multiplicities : (A, Int)*)

}

/** Provides data-types used in JSON-schema */
package object Datatypes {

    import scala.language.implicitConversions

    /** Implicit conversion of any [[Real]] to a scala floting point number */
    implicit def realToFloat(x : Real) : Float = x match {
        case Nat(x) => x.floatValue
        case Integer(x) => x.floatValue
        case Floating(x) => x
        case Ratio(Integer(x), Integer(y)) => x.floatValue / y.floatValue
    }
    
}