package io.github.torsteinvik.zetatypes.db.oeis

import io.github.torsteinvik.zetatypes.db._
import io.github.torsteinvik.zetatypes.db.Datatypes._

import org.json4s._

object Converter{
    private implicit val formats = DefaultFormats
    
    def apply(json : JObject) : MultiplicativeFunction = {
    }
}
