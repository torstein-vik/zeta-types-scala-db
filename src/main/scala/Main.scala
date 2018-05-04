package io.github.torsteinvik.zetatypes.db

object Main extends App with REPL {
    
    import query.Property._
    
    // Download and store all oeis multiplicative functions in db, and use b-files for extra data
    // downloadAndSaveOEIS()
    
    // Print simple bell tables for all multiplicative functions in db
    // DBBellTables()
    
    // All multiplicative functions where 24 and 51 are fix-points
    // mfquery(belltable where (mfvalue(24) ==? 24 and mfvalue(51) ==? 51)).print()
    
    def downloadAndSaveOEIS() {
        val multsInOEIS : Seq[MultiplicativeFunction] = {
            import oeis._
            
            // Download json from oeis.org
            val download = Download()
            // Convert json into lsit of MultiplicativeFunction
            val conv = download.map(Converter.apply)
            
            // Return this as multsInOEIS
            conv
        }
        
        // Batch-add multsInOEIS
        mfbatch(multsInOEIS)
        
        println(multsInOEIS)
    }
    
    def DBBellTables() {
        val all = mfgetall
        
        all foreach {mf => println(mf.bellTableText()); println()}
    }
    
    // Close database
    close()
}
