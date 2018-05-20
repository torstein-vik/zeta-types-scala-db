package io.github.torsteinvik.zetatypes.test.db

import org.scalatest.FunSuite

import io.github.torsteinvik.zetatypes.db.query.parsing.Parser._
import io.github.torsteinvik.zetatypes.db.query.Property._
import io.github.torsteinvik.zetatypes.db.query._
import io.github.torsteinvik.zetatypes.db.Datatypes._
import io.github.torsteinvik.zetatypes.db._

class ParserTest extends FunSuite {
    
    test ("Parsing simple queries") {
        
        assert(parse("mf") === (mf : Query[_]))
        assert(parse("batchid") === (batchid : Query[_]))
        assert(parse("mflabel") === (mflabel : Query[_]))
        assert(parse("name") === (name : Query[_]))
        assert(parse("definition") === (definition : Query[_]))
        assert(parse("comments") === (comments : Query[_]))
        assert(parse("properties") === (properties : Query[_]))
        assert(parse("belltable") === (belltable : Query[_]))
        
        assert(parse("bellcell(11, 34)") === (bellcell(11, 34) : Query[_]))
        assert(parse("bellrow(7)") === (bellrow(7) : Query[_]))
        assert(parse("bellsmalltable(12, 12)") === (bellsmalltable(12, 12) : Query[_]))
        
    }
    
}
