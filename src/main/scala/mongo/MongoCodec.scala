package io.github.torsteinvik.zetatypes.db.mongo

import org.json4s._
import org.json4s.native.JsonMethods._

import org.mongodb.scala._

object MongoCodec {
    def encode (x : JValue) : Document = Document(compact(render(x)))
    def decode (x : Document) : JValue = parse(x.toJson)
}
