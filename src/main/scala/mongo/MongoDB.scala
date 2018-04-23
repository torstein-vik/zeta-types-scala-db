package io.github.torsteinvik.zetatypes.db.mongo

import io.github.torsteinvik.zetatypes.db._

import org.mongodb.scala._

class MongoDB (address : String, database : String, collection : String) extends Database {
    val client : MongoClient = MongoClient(address)
    val db : MongoDatabase = client.getDatabase(database)
    val zetatypes : MongoCollection[Document] = db.getCollection(collection)
    
    def close() = {client.close();}
    
    def store(mf : MultiplicativeFunction) : String = ???
    def getByMFLabel(mflabel : String) : MultiplicativeFunction = ???
    
}

object MongoDB {
    def apply(
        address : String = MongoConfig.address, 
        database : String = MongoConfig.dbName, 
        collection : String = MongoConfig.collectionName
    ) = new MongoDB(address, database, collection)
}
