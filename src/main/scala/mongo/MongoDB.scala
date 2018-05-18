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
    
    private def sync[T](ob : Observable[T]) : Seq[T] = Await.result(ob.toFuture(), Duration(60, TimeUnit.SECONDS))
    
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
    
    def query[T](query : Query[T]) : QueryResult[T] = new QueryResult(sync{
            
            val projection = ProjectionAssembly(query.requirements.minimal)
            
            zetatypes.find().projection(projection).map { doc : Document => 
                val multfunc = MongoCodec.decode(doc)
                
                val provider = query.requirements.assembleProvider( new QueryPointer {
                    lazy val bellTable = decode[Seq[(Prime, Seq[ComplexNumber])]](belltable.path.foldLeft(multfunc)(_ \ _))
                    
                    def evalMFProperty[S](q : MFProperty[S]) : S = q match {
                        case `belltable` => bellTable
                        case bellcell(p, Nat(e)) => bellTable.find(_._1 == p).map(_._2.lift(e.toInt)).flatten
                        case bellrow(p) => bellTable.find(_._1 == p).map(_._2)
                        case bellsmalltable(ps, es) => bellTable.take(ps).map{case (p, vals) => (p, vals.take(es))}
                        case q @ JSONProperty(path) => decode[S](path.foldLeft(multfunc)(_ \ _))(q.codec)
                    }
                })
                
                DirectQuery.queryOne(query)(provider)
            }
    }.flatten)
    
    
    object ProjectionAssembly {
        import org.bson.conversions.Bson
        import org.mongodb.scala.model.Projections._
        
        private sealed abstract class FieldProjection[T] {def field : Field[T] }
        private case class Field[T] (property : JSONProperty[T]) extends FieldProjection[T] { def field = this}
        private case class Slice[T] (field : Field[Seq[T]], skip : Int, amount : Int) extends FieldProjection[Seq[T]]
        
        private def union (f1 : FieldProjection[_], f2 : FieldProjection[_]) : FieldProjection[_] = {require(f1.field == f2.field); (f1, f2) match {
            case (Slice(f, skip1, amt1), Slice(_, skip2, amt2)) => {
                val skip = math.min(skip1, skip2)
                val amt = math.max(skip1 + amt1, skip2 + amt2) - skip
                Slice(f, skip, amt)
            }
            case (f : Field[_], _) => f
            case (_, f : Field[_]) => f
        }}
        
        import io.github.torsteinvik.zetatypes.db.dbmath._
        private def toProjection (property : MFProperty[_]) : FieldProjection[_] = property match {
            case property : JSONProperty[_] => Field(property)
            case bellcell(Prime(p), _) => Slice(Field(belltable), Primes.indexOf(p.toInt), 1)
            case bellrow(Prime(p)) => Slice(Field(belltable), Primes.indexOf(p.toInt), 1)
            case bellsmalltable(ps, _) => Slice(Field(belltable), 0, ps)
        }
        
        private def toBsonProjection (p : FieldProjection[_]) : Bson = p match {
            case Field(JSONProperty(path)) => include(path.mkString("."))
            case Slice(Field(JSONProperty(path)), skip, amt) => slice(path.mkString("."), skip, amt)
        }
        
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
