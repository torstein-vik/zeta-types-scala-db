package io.github.torsteinvik.zetatypes.test.db

import org.scalatest.FunSuite

import io.github.torsteinvik.zetatypes.db._
import io.github.torsteinvik.zetatypes.db.Datatypes._
import io.github.torsteinvik.zetatypes.db.codec._

class CodecTest extends FunSuite {
    
    test("Define codec for MultiplicativeFunction") { Codec[MultiplicativeFunction] }
    
    test("Encode MultiplicativeFunction"){
        val codec = Codec[MultiplicativeFunction]
    }
    
}
