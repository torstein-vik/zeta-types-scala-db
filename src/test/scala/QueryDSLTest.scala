package io.github.torsteinvik.zetatypes.test.db

import org.scalatest.FunSuite

import io.github.torsteinvik.zetatypes.db._
import io.github.torsteinvik.zetatypes.db.Query._

class QueryDSLTest extends FunSuite {
    test("basic queries") {
        mf : Query[MultiplicativeFunction]
        mf ~ mf ~ mf : Query[MultiplicativeFunction ~ MultiplicativeFunction ~ MultiplicativeFunction]
    }
}
