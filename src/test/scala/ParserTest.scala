package io.github.torsteinvik.zetatypes.test.db

import org.scalatest.FunSuite

import io.github.torsteinvik.zetatypes.db.query.parsing.Parser._
import io.github.torsteinvik.zetatypes.db.query.Property._
import io.github.torsteinvik.zetatypes.db.Datatypes._
import io.github.torsteinvik.zetatypes.db._

class ParserTest extends FunSuite {
    
    
    test ("Parsing literals") {
        assert(parse("342")(literal[Int]) === 342)
        assert(parse("-342")(literal[Int])  === -342)
        assert(parse("342")(literal[Integer]) === Integer(342))
        assert(parse("-342")(literal[Integer]) === Integer(-342))
        assert(parse("12")(literal[Nat]) === Nat(12))
        assert(parse("53")(literal[Nat]) === Nat(53))
        assert(parse("17")(literal[Prime]) === Prime(17))
        assert(parse("3")(literal[Prime]) === Prime(3))
        assert(parse("""" Hey """")(literal[String]) === " Hey ")
        assert(parse("""" Hey \" \r \\"""")(literal[String])  === " Hey \" \r \\")
    }
    
}
