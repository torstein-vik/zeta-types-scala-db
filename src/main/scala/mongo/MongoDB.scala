package io.github.torsteinvik.zetatypes.db.mongo

import io.github.torsteinvik.zetatypes.db._
import io.github.torsteinvik.zetatypes.db.Datatypes._
import io.github.torsteinvik.zetatypes.db.codec._
import io.github.torsteinvik.zetatypes.db.query._
import io.github.torsteinvik.zetatypes.db.query.Property._

import org.mongodb.scala._

import java.util.concurrent.TimeUnit

import java.time.Instant

import org.json4s._

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
    
    def batch(mfs : Seq[MultiplicativeFunction], batchId : String = null) : Unit = {
        val time = Instant.now.getEpochSecond.toString
        val nBatchId = if(batchId == null) "#" + (mfs##).toHexString + " - " + mfs.length else batchId
        
        val nmfs = for (mf <- mfs) yield {
            val meta = mf.metadata
            val nMeta = meta.copy(
                firstAddedTimestamp = if(meta.firstAddedTimestamp.isEmpty) Some(time) else meta.firstAddedTimestamp,
                lastChangedTimestamp = Some(time),
                batchId = Some(nBatchId)
            )
            
            mf.copy(metadata = nMeta)
        }
        sync(zetatypes.insertMany(nmfs.map(toDoc[MultiplicativeFunction])))
    }
    
    def store(mf : MultiplicativeFunction) : Unit = {
        val time = Instant.now.getEpochSecond.toString
        val batchId = "#" + (mf##).toHexString + " - 1" 
        
        val meta = mf.metadata
        val nMeta = meta.copy(
            firstAddedTimestamp = if(meta.firstAddedTimestamp.isEmpty) Some(time) else meta.firstAddedTimestamp,
            lastChangedTimestamp = Some(time),
            batchId = if(meta.batchId.isEmpty) Some(batchId) else meta.batchId
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
    
    def query[T](query : Query[T]) : QueryResult[T] = {
        val mfs : Seq[JValue] = sync(zetatypes.find()).map(MongoCodec.decode)
        
        val reqs = query.requirements.createProvidersFromPointers(mfs.map(multfunc => new QueryPointer {
        }))
        
        DirectQuery.query(query)(reqs)
    }
    
    def getAll : Seq[MultiplicativeFunction] = sync(zetatypes.find()).map(fromDoc[MultiplicativeFunction]).sortBy(_.mflabel)
    def length : Int = sync(zetatypes.count())(0).toInt
    
}

object MongoDB {
    def apply(
        address : String = MongoConfig.address, 
        database : String = MongoConfig.dbName, 
        collection : String = MongoConfig.collectionName
    ) = new MongoDB(address, database, collection)
}
