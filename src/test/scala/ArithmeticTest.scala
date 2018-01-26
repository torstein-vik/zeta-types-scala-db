package io.github.torsteinvik.zetatypes.test.db

import org.scalatest.FunSuite

import io.github.torsteinvik.zetatypes.db.Arithmetic._

class ArithmeticTest extends FunSuite {
    
    test ("Natural number requirements") {
        assertThrows[IllegalArgumentException]{ Nat(-10) }
        assertThrows[IllegalArgumentException]{ Nat(0) }
        ;{ Nat(1) }
        ;{ Nat(10) }
    }
    
    test ("Ratio requirements") {
        assertThrows[IllegalArgumentException]{ Ratio(Integer(0), Integer(0)) }
        assertThrows[IllegalArgumentException]{ Ratio(Integer(10), Integer(0)) }
        ;{ Ratio(Integer(10), Integer(1)) }
        ;{ Ratio(Integer(10), Integer(-15)) }
    }
    
    test ("Complex Polar requirements") {
        assertThrows[IllegalArgumentException]{ PolarComplex(Integer(-10), Integer(0)) }
        assertThrows[IllegalArgumentException]{ PolarComplex(Integer(-10), Floating(0.5F)) }
        assertThrows[IllegalArgumentException]{ PolarComplex(Floating(-0.00001F), Floating(0.5F)) }
        ;{ PolarComplex(Integer(0), Integer(0)) }
        ;{ PolarComplex(Integer(10), Integer(0)) }
        ;{ PolarComplex(Integer(10), Floating(0.5F)) }
        
        assertThrows[IllegalArgumentException]{ PolarComplex(Integer(10), Integer(-1)) }
        assertThrows[IllegalArgumentException]{ PolarComplex(Integer(10), Floating(-5F)) }
        assertThrows[IllegalArgumentException]{ PolarComplex(Integer(10), Floating(-0.00001F)) }
        assertThrows[IllegalArgumentException]{ PolarComplex(Integer(10), Floating(1.00001F)) }
        assertThrows[IllegalArgumentException]{ PolarComplex(Integer(10), Floating(5F)) }
        assertThrows[IllegalArgumentException]{ PolarComplex(Integer(10), Integer(2)) }
        ;{ PolarComplex(Integer(10), Integer(0)) }
        ;{ PolarComplex(Integer(10), Ratio(Integer(2), Integer(4))) }
        ;{ PolarComplex(Integer(10), Floating(0.5F)) }
    }
    
}
