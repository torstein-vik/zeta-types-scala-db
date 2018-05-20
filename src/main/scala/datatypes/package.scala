package io.github.torsteinvik.zetatypes.db

/** This pacakge provides various datatypes used throughout the project
 *  @author Torstein Vik
 */
package object datatypes extends LowerPriorityImplicits1 {

    import scala.language.implicitConversions

    /** Implicit conversion of any [[Real]] to a scala floting point number */
    implicit def realToDouble(x : Real) : Double = x match {
        case Prime(x) => x.doubleValue
        case Nat(x) => x.doubleValue
        case Integer(x) => x.doubleValue
        case Floating(x) => x
        case Ratio(Integer(x), Integer(y)) => x.doubleValue / y.doubleValue
    }
    
    implicit def floatToComplex(x : Float) : ComplexNumber = Floating(x.doubleValue)
    implicit def doubleToComplex(x : Double) : ComplexNumber = Floating(x)
    implicit def intToComplex(x : Int) : ComplexNumber = Integer(x)
    
    type PrimeLogSymbol = HybridSet[(Real, Real)]
    
}

trait LowerPriorityImplicits1 extends LowerPriorityImplicits2{
    import datatypes._
    import scala.language.implicitConversions
    
    implicit def floatToReal(x : Float) : Real = Floating(x.doubleValue)
    implicit def doubleToReal(x : Double) : Real = Floating(x)
    implicit def intToReal(x : Int) : Real = Integer(x)
}

trait LowerPriorityImplicits2 {
    import datatypes._
    import scala.language.implicitConversions
    
    implicit def intToInteger(x : Int) : Integer = Integer(x)
    implicit def intToPrime(x : Int) : Prime = Prime(x)
    implicit def intToNat(x : Int) : Nat = Nat(x)
}