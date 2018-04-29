package io.github.torsteinvik.zetatypes.test.db

import org.scalatest.FunSuite

import io.github.torsteinvik.zetatypes.db._
import io.github.torsteinvik.zetatypes.db.Datatypes._
import io.github.torsteinvik.zetatypes.db.query._
import io.github.torsteinvik.zetatypes.db.query.Property._

class QueryDSLTest extends FunSuite {
    test("basic queries") {
        mf : Query[MultiplicativeFunction]
        mf ~ mflabel ~ mfvalue(2) : Query[MultiplicativeFunction ~ String ~ ComplexNumber]
        
        mflabel ~ mf where mf === mf : Query[String ~ MultiplicativeFunction]
        mflabel ~ mf where (mf === mf and mf === mf) : Query[String ~ MultiplicativeFunction]
        mflabel ~ mf where (mf === mf or mf === mf) : Query[String ~ MultiplicativeFunction]
        mflabel ~ mf where (mf === mf & mf === mf) : Query[String ~ MultiplicativeFunction]
        mflabel ~ mf where (mf === mf | mf === mf) : Query[String ~ MultiplicativeFunction]
        
        mflabel where (definition contains "eta") : Query[String]
        mflabel where (name matches """A0\d+""".r) : Query[String]
        mflabel where (comments has (_ contains "A000006")) : Query[String]
        mflabel where (mflabel contains name) : Query[String]
        mflabel where (("A000005A000006" : Property[String]) contains name) : Query[String]
        mflabel where (keywords contains "oeis_nonn") : Query[String]
        mflabel where (mfvalue(2) === 1) : Query[String]
        mflabel where (!(mfvalue(2) === 1)) : Query[String]
        mflabel where (mfvalue(2) !== 1) : Query[String]
        mflabel where (mfbell(Prime(2), 3) === 1) : Query[String]
        mflabel where (mfbell(Prime(2), 3) === 1 and mfbell(Prime(3), 3) === 1) : Query[String]
    }
}
