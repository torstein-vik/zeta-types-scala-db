package io.github.torsteinvik.zetatypes.db

object REPL {
    
    private object dbstate {
        private var db : Database = null
        def getDB = db
        def setDB (db: Database) = {this.db = db}
    }
    
    def use(db : Database) : Unit = {dbstate.setDB(db)}
    
    def mfstore(mf : MultiplicativeFunction) : String = ???
    
    def mfget(mflabel : String) : MultiplicativeFunction = ???
}
