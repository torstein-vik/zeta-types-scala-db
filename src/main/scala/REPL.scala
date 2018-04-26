package io.github.torsteinvik.zetatypes.db

import mongo._

trait REPL {
    
    use(MongoDB())
    
    private object dbstate {
        private var db : Database = null
        def getDB = db : Database
        def setDB (db: Database) : Unit = {this.db = db}
    }
    
    def use(db : Database) : Unit = {dbstate.setDB(db)}
    
    def mfstore(mf : MultiplicativeFunction) : String = ???
    
    def mfget(mflabel : String) : MultiplicativeFunction = ???
}

object REPL extends REPL
