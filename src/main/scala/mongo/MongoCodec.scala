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
        case JInt(num) if num.abs <= Int.MaxValue => new BsonInt32(num.toInt)
        case JDouble(num) => new BsonDouble(num)
        case JBool(b) => new BsonBoolean(b)
        case JArray(lst) => new BsonArray(lst.map(encodeBson(_)).asJava)
        case JObject(lst) => new BsonDocument(lst.map{case JField(str, jval) => new BsonElement(str, encodeBson(jval))}.asJava)
        case JNull => new BsonNull()
        
        case _ => throw new Exception(f"Unhandled JValue in MongoCodec: $x")
    }
    
    // TODO: Look into using : @switch, maybe it will speed things up
    // Unhandled: BINARY, DATE_TIME, DB_POINTER, END_OF_DOCUMENT, INT64, JAVASCRIPT, JAVASCRIPT_WITH_SCOPE, MAX_KEY, MIN_KEY, REGULAR_EXPRESSION, SYMBOL, TIMESTAMP, UNDEFINED
    def decodeBson (x : BsonValue) : JValue = (x.getBsonType) match {
        case STRING => JString(x.asString.getValue())
        case INT32 => JInt(x.asInt32.getValue())
        case DOUBLE => JDouble(x.asDouble.getValue())
        case BOOLEAN => JBool(x.asBoolean.getValue())
        case ARRAY => JArray(x.asArray.asScala.toList.map(decodeBson(_)))
        case DOCUMENT => JObject(x.asDocument.asScala.toList.map{case (str, bval) => JField(str, decodeBson(bval))})
        case NULL => JNull
        
        case OBJECT_ID => JString(x.asObjectId.getValue().toHexString())
        
        case _ => throw new Exception(f"Unhandled BsonValue in MongoCodec: $x of type ${x.getBsonType}")
    }
    
    def encode (x : JValue) : Document = new Document(encodeBson(x).asDocument)
    def decode (x : Document) : JValue = decodeBson(x.toBsonDocument)
}
