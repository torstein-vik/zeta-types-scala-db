package io.github.torsteinvik.zetatypes.db

import mongo._
import query._

trait REPL {
    
    use(MongoDB())
    
    private object dbstate {
        private var db : Database = null
        def getDB = db : Database
        def setDB (db: Database) : Unit = {close(); this.db = db}
    }
    
    def db : Database = dbstate.getDB
    
    def close() : Unit = if (db != null) db.close()
    
    def use(db : Database) : Unit = {dbstate.setDB(db)}
    
    def mfstore(mf : MultiplicativeFunction, batchid : Option[String] = None, time : Option[String] = None) = db.store(mf, batchid, time)
    def mfbatch(mfs : Seq[MultiplicativeFunction], batchid : Option[String] = None, time : Option[String] = None) = db.batch(mfs, batchid, time)
    
    def mfget(mflabel : String) : MultiplicativeFunction = db.get(mflabel)
    def mfgetall : Seq[MultiplicativeFunction] = db.getAll
    
    def mfquery[T](q : Query[T]) : QueryResult[T] = db.query(q)
}
