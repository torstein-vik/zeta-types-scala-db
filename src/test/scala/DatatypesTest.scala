package io.github.torsteinvik.zetatypes.test.db

import org.scalatest.FunSuite

import io.github.torsteinvik.zetatypes.db.datatypes._

class DatatypesTest extends FunSuite {
    
    test ("Natural number requirements") {
        assertThrows[IllegalArgumentException]{ Nat(-10) }
        assertThrows[IllegalArgumentException]{ Nat(-1) }
        ;{ Nat(0) }
        ;{ Nat(10) }
        
        { new Nat(-1) }
    }
    
    test ("Prime number requirements") {
        assertThrows[IllegalArgumentException]{ Prime(1) }
        assertThrows[IllegalArgumentException]{ Prime(-1) }
        assertThrows[IllegalArgumentException]{ Prime(0) }
        assertThrows[IllegalArgumentException]{ Prime(20) }
        assertThrows[IllegalArgumentException]{ Prime((179425177 : BigInt) * 179425177) }
        ;{ Prime(3) }
        ;{ Prime(17) }
        ;{ Prime(97) }
        ;{ Prime(179425177) }
        
        { new Prime(6) }
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
