package io.github.torsteinvik.zetatypes.db

object Main extends App with REPL {
    
    import query.Property._
    
    // Download and store all oeis multiplicative functions in db, and use b-files for extra data
    // oeis.Manager(mfstore, useBFile = true)
    
    // Print simple bell tables for all multiplicative functions in db
    // DBBellTables()
    
    // All multiplicative functions where 24 and 51 are fix-points
    // mfquery(pretty() where (mfvalue(24) ==? 24 and mfvalue(51) ==? 51)).print()
    
    def DBBellTables() {
        val all = mfgetall
        
        all foreach {mf => println(mf.bellTableText()); println()}
    }
    
    // Close database
    close()
}
