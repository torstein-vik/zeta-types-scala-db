package io.github.torsteinvik.zetatypes.test.db

import org.scalatest.FunSuite

import io.github.torsteinvik.zetatypes.db._
import io.github.torsteinvik.zetatypes.db.Query._
import io.github.torsteinvik.zetatypes.db.Property._

class QueryDSLTest extends FunSuite {
    test("basic queries") {
        mf : Query[MultiplicativeFunction]
        mf ~ mflabel ~ mf : Query[MultiplicativeFunction ~ String ~ MultiplicativeFunction]
        
        mflabel ~ mf where mf === mf : Query[String ~ MultiplicativeFunction]
        mflabel ~ mf where (mf === mf and mf === mf) : Query[String ~ MultiplicativeFunction]
        mflabel ~ mf where (mf === mf or mf === mf) : Query[String ~ MultiplicativeFunction]
    }
}
