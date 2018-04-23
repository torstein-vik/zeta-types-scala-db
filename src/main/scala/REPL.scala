package io.github.torsteinvik.zetatypes.db

object REPL {
    
    private object dbstate {
        private var db : Database = null
        def getDB = db
        def setDB (db: Database) = {this.db = db}
    }
    
}
