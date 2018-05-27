package io.github.torsteinvik.zetatypes.test.db

import org.scalatest.FunSuite

import io.github.torsteinvik.zetatypes.db.mongo.MongoCodec

import org.json4s._
import org.json4s.native.JsonMethods._

import org.mongodb.scala._
import org.mongodb.scala.bson.{Document => _, _}

import scala.collection.JavaConverters._

class MongoCodecTest extends FunSuite {
    test ("MongoCodec for documents") {
        val json = """
        {
            "hey": "hey",
            "hola": 12.34,
            "hola2": -1234235345348573495634756347563475637635796537945637942.34,
            "test": null,
            "bool": false,
            "arr": [{"check": ["test1", "test2"]}, 12, [12, 4, []]]
        }
        """
        
        val doc = Document(json) 
        val json4s = parse(json)
        
        assert(MongoCodec.encode(json4s) === doc)
        assert(MongoCodec.decode(doc) === json4s)
        
        
    }
    
    test ("MongoCodec encode to Bson") {
        
        assert(MongoCodec.encodeBson(JString("hey")).asString.getValue() === "hey")
        assert(MongoCodec.encodeBson(JInt(12)).asNumber.intValue() === 12)
        assert(MongoCodec.encodeBson(JDouble(12302.4553)).asNumber.doubleValue() === 12302.4553)
        assert(MongoCodec.encodeBson(JNull).isNull())
        assert(MongoCodec.encodeBson(JBool(true)).asBoolean.getValue() === true)
        assert(MongoCodec.encodeBson(JArray(List(JInt(12), JInt(24), JInt(34)))).asArray.getValues().asScala.map(_.asNumber.doubleValue()) === Seq(12, 24, 34))
                
    }
}
