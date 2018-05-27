package io.github.torsteinvik.zetatypes.db.mongo

import org.json4s._

import org.mongodb.scala.bson._
import org.bson.BsonType._

import scala.collection.JavaConverters._

// TODO: Implement a better codec.
object MongoCodec {
    
    def encode (x : JValue) : Document = new Document(encodeBson(x).asDocument)
    def decode (x : Document) : JValue = decodeBson(x.toBsonDocument)
}
