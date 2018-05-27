package io.github.torsteinvik.zetatypes.db.mongo

import org.json4s._

import org.mongodb.scala.bson._
import org.bson.BsonType._

import scala.collection.JavaConverters._

// TODO: Implement a better codec.
object MongoCodec {
    
    // Unhandled: JNothing, JDecimal, JInt out of INT32 bounds
    def encodeBson (x : JValue) : BsonValue = x match {
        case JString(str) => new BsonString(str)
        case JInt(num) if num.abs < Int.MaxValue => new BsonInt32(num.toInt)
        case JDouble(num) => new BsonDouble(num)
        case JBool(b) => new BsonBoolean(b)
        case JArray(lst) => new BsonArray(lst.map(encodeBson(_)).asJava)
        case JObject(lst) => new BsonDocument(lst.map{case JField(str, jval) => new BsonElement(str, encodeBson(jval))}.asJava)
        case JNull => new BsonNull()
        
        case _ => throw new Exception(f"Unhandled JValue in MongoCodec: $x")
    }
    
    def encode (x : JValue) : Document = new Document(encodeBson(x).asDocument)
    def decode (x : Document) : JValue = decodeBson(x.toBsonDocument)
}
