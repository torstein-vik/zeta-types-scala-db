package io.github.torsteinvik.zetatypes.test.db

import org.scalatest.FunSuite

import io.github.torsteinvik.zetatypes.db.query.parsing.Parser._
import io.github.torsteinvik.zetatypes.db.query.Property._
import io.github.torsteinvik.zetatypes.db.Datatypes._
import io.github.torsteinvik.zetatypes.db._

class ParserTest extends FunSuite {
    
    test ("Parsing mfproperties") {
        
        assert(parse("mf")(mfproperty[MultiplicativeFunction]) == mf)
        assert(parse("batchid")(mfproperty[Option[String]]) == batchid)
        assert(parse("mflabel")(mfproperty[String]) == mflabel)
        assert(parse("name")(mfproperty[String]) == name)
        assert(parse("definition")(mfproperty[String]) == definition)
        assert(parse("comments")(mfproperty[Seq[String]]) == comments)
        assert(parse("properties")(mfproperty[Record[Boolean]]) == properties)
        assert(parse("belltable")(mfproperty[Seq[(Prime, Seq[ComplexNumber])]]) == belltable)
        
        assert(parse("bellcell(11, 34)")(mfproperty[Option[ComplexNumber]]) == bellcell(11, 34))
        assert(parse("bellrow(7)")(mfproperty[Option[Seq[ComplexNumber]]]) == bellrow(7))
        assert(parse("bellsmalltable(12, 12)")(mfproperty[Seq[(Prime, Seq[ComplexNumber])]]) == bellsmalltable(12, 12))
        
    }
    
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
