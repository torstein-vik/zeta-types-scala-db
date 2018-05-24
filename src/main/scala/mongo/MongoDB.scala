package io.github.torsteinvik.zetatypes.db.mongo

import io.github.torsteinvik.zetatypes.db._
import io.github.torsteinvik.zetatypes.db.datatypes._
import io.github.torsteinvik.zetatypes.db.codec._
import io.github.torsteinvik.zetatypes.db.query._

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
    
    private def sync[T](ob : Observable[T]) : Seq[T] = Await.result(ob.toFuture(), Duration(60, TimeUnit.SECONDS))
    
    private def processMF(mf : MultiplicativeFunction, batchid : Option[String], time : String, batchFallback : String) : MultiplicativeFunction = mf.copy(
        metadata = mf.metadata.copy(
            firstAddedTimestamp = Some(mf.metadata.firstAddedTimestamp.getOrElse(time)),
            lastChangedTimestamp = Some(time),
            batchId = Some(batchid.getOrElse(mf.metadata.batchId.getOrElse(batchFallback)))
        )
    )
    
    def close() = client.close()
    
    def batch(mfs : Seq[MultiplicativeFunction], batchid : Option[String] = None, time : Option[String] = None) : Unit = {
        val altTime = Instant.now.getEpochSecond.toString
        val altBatchId = f"#${mfs.##}%X - ${mfs.length}"
        
        val nmfs = for (mf <- mfs) yield processMF(mf, batchid, time.getOrElse(altTime), altBatchId)
        sync(zetatypes.insertMany(nmfs.map(toDoc[MultiplicativeFunction])))
    }
    
    def store(mf : MultiplicativeFunction, batchid : Option[String] = None, time : Option[String] = None) : Unit = batch(Seq(mf), batchid, time)
    
    def get(mflabel : String) : MultiplicativeFunction = sync {
        import org.mongodb.scala.model.Filters._
        zetatypes.find(equal("mflabel", mflabel))
    } match {
        case Seq() => throw new Exception("Didn't find: " + mflabel)
        case Seq(x) => fromDoc[MultiplicativeFunction](x)
        case _ => throw new Exception("Many with this label: " + mflabel)
    }
    
    def query[T](query : Query[T]) : QueryResult[T] = QueryTools.aggregate(query)(new QueryResult(sync{
            
            val projection = ProjectionAssembly(query.requirements.minimal)
            
            zetatypes.find().projection(projection).map { doc : Document => 
                val multfunc = MongoCodec.decode(doc)
                
                val provider = query.requirements.assembleProvider( new QueryPointer {
                    val cache = scala.collection.mutable.Map[Seq[String], Any]()
                    def byJsonPath[S](q : JSONProperty[S]) : S = cache.getOrElseUpdate(q.path, decode[S](q.path.foldLeft(multfunc)(_ \ _))(q.codec)).asInstanceOf[S]
                    
                    def evalMFProperty[S](q : MFProperty[S]) : S = q match {
                        case q : JSONProperty[_] => byJsonPath(q)
                        case bellcell(p, Nat(e)) => evalMFProperty(belltable).find(_._1 == p).map(_._2.lift(e.toInt)).flatten
                        case bellrow(p) => evalMFProperty(belltable).find(_._1 == p).map(_._2).getOrElse(Seq())
                        case bellsmalltable(ps, es) => evalMFProperty(belltable).take(ps).map{case (p, vals) => (p, vals.take(es))}
                    }
                })
                
                QueryTools.filterAndProjectOne(query)(provider)
            }
    }.flatten))
    
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
