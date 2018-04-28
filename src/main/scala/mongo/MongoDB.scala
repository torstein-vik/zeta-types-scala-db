package io.github.torsteinvik.zetatypes.db.mongo

import io.github.torsteinvik.zetatypes.db._
import io.github.torsteinvik.zetatypes.db.codec._
import io.github.torsteinvik.zetatypes.db.query._

import org.mongodb.scala._

import java.util.concurrent.TimeUnit

import java.time.Instant

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class MongoDB (address : String, database : String, collection : String) extends Database {
    val client : MongoClient = MongoClient(address)
    val db : MongoDatabase = client.getDatabase(database)
    val zetatypes : MongoCollection[Document] = db.getCollection(collection)
    
    private def toDoc[T](x : T)(implicit codec : Codec[T]) : Document = MongoCodec.encode(encode[T](x))
    private def fromDoc[T](x : Document)(implicit codec : Codec[T]) : T = decode[T](MongoCodec.decode(x))
    
    private def sync[T](ob : Observable[T]) : Seq[T] = Await.result(ob.toFuture(), Duration(10, TimeUnit.SECONDS))
    
    def close() = {client.close();}
    
    def batch(mfs : Seq[MultiplicativeFunction], batchid : String = null) : Unit = ???
    def store(mf : MultiplicativeFunction) : Unit = {
        val time = Instant.now.getEpochSecond.toString
        val meta = mf.metadata
        val nMeta = meta.copy(
            firstAddedTimestamp = if(meta.firstAddedTimestamp.isEmpty) Some(time) else meta.firstAddedTimestamp,
            lastChangedTimestamp = Some(time),
        )
        val nmf = mf.copy(metadata = nMeta)
        sync(zetatypes.insertOne(toDoc(nmf)))
    }
    
    def get(mflabel : String) : MultiplicativeFunction = fromDoc[MultiplicativeFunction](sync {
        import org.mongodb.scala.model.Filters._
        zetatypes.find(equal("mflabel", mflabel))
    } match {
        case Seq() => throw new Exception("Didn't find: " + mflabel)
        case Seq(x) => x
        case _ => throw new Exception("Many with this label: " + mflabel)
    })
    
    def query[T](query : Query[T]) : T = ???
    
    def getAll : Seq[MultiplicativeFunction] = sync(zetatypes.find()).map(fromDoc[MultiplicativeFunction])
    def length : Int = sync(zetatypes.count())(0).toInt
    
}

object MongoDB {
    def apply(
        address : String = MongoConfig.address, 
        database : String = MongoConfig.dbName, 
        collection : String = MongoConfig.collectionName
    ) = new MongoDB(address, database, collection)
}
