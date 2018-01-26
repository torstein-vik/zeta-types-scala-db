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
    
}
