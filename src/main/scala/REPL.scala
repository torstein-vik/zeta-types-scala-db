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
    
    def mfstore(mf : MultiplicativeFunction) = db.store(mf)
    def mfbatch(mfs : Seq[MultiplicativeFunction]) = db.batch(mfs)
    
    def mfget(mflabel : String) : MultiplicativeFunction = db.get(mflabel)
    def mfgetall : Seq[MultiplicativeFunction] = db.getAll
    
    def mfquery[T](q : Query[T]) : QueryResult[T] = db.query(q)
}

object REPL extends REPL
