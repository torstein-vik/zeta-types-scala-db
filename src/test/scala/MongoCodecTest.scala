package io.github.torsteinvik.zetatypes.test.db

import org.scalatest.FunSuite

import io.github.torsteinvik.zetatypes.db.mongo.MongoCodec

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
        
        import org.json4s._
        import org.json4s.native.JsonMethods._
        
        import org.mongodb.scala._
        
        val doc = Document(json) 
        val json4s = parse(json)
        
        assert(MongoCodec.encode(json4s) === doc)
        assert(MongoCodec.decode(doc) === json4s)
        
        
    }
}
