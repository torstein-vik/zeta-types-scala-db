package io.github.torsteinvik.zetatypes.db.mongo

import io.github.torsteinvik.zetatypes.db._
import io.github.torsteinvik.zetatypes.db.codec._
import io.github.torsteinvik.zetatypes.db.query._

import org.mongodb.scala._

import java.util.concurrent.TimeUnit

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class MongoDB (address : String, database : String, collection : String) extends Database {
    val client : MongoClient = MongoClient(address)
    val db : MongoDatabase = client.getDatabase(database)
    val zetatypes : MongoCollection[Document] = db.getCollection(collection)
    
    def close() = {client.close();}
    private def sync[T](ob : Observable[T]) : Seq[T] = Await.result(ob.toFuture(), Duration(10, TimeUnit.SECONDS))
    
    def batch(mfs : Seq[MultiplicativeFunction], batchid : String = null) : Unit = ???
    def store(mf : MultiplicativeFunction) : Unit = {
        val doc : Document = MongoCodec.encode(encode(mf))
        sync(zetatypes.insertOne(doc))
    }
    def get(mflabel : String) : MultiplicativeFunction = ???
    
    def query[T](query : Query[T]) : T = ???
    
    def getAll : Seq[MultiplicativeFunction] = sync(zetatypes.find()).map(x => decode[MultiplicativeFunction](MongoCodec.decode(x)))
    def length : Int = ???
    
}

object MongoDB {
    def apply(
        address : String = MongoConfig.address, 
        database : String = MongoConfig.dbName, 
        collection : String = MongoConfig.collectionName
    ) = new MongoDB(address, database, collection)
}
