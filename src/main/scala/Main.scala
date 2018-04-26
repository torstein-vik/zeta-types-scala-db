package io.github.torsteinvik.zetatypes.db

object Main extends App with REPL {
    
    
    def DBBellTables() {
        val all = mfgetall
        
        all foreach {mf => println(mf.bellTableText()); println()}
    }
    
}
