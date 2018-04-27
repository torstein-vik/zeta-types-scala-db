package io.github.torsteinvik.zetatypes.db

import mongo._

trait REPL {
    
    use(MongoDB())
    
    private object dbstate {
        private var db : Database = null
        def getDB = db : Database
        def setDB (db: Database) : Unit = {this.db = db}
    }
    
    def db : Database = dbstate.getDB
    
    def use(db : Database) : Unit = {dbstate.setDB(db)}
    
    def mfstore(mf : MultiplicativeFunction) = db.store(mf)
    
    def mfget(mflabel : String) : MultiplicativeFunction = db.get(mflabel)
    def mfgetall : Seq[MultiplicativeFunction] = db.getAll
}

object REPL extends REPL
