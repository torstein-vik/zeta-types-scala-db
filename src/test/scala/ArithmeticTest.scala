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
    
}
