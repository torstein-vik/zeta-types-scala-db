package io.github.torsteinvik.zetatypes.db.mongo

import org.json4s._
import org.json4s.native.JsonMethods._

import org.mongodb.scala.bson._

// TODO: Implement a better codec.
object MongoCodec {
    def encodeBson (x : JValue) : BsonValue = encode(JObject(List(JField("value", x)))).apply[BsonValue]("value")
    def decodeBson (x : BsonValue) : JValue = decode(Document("value" -> x)) \ "value"
    
    def encode (x : JValue) : Document = Document(compact(render(x)))
    def decode (x : Document) : JValue = parse(x.toJson)
}
