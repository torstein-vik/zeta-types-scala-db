package io.github.torsteinvik.zetatypes.db

object Main extends App with REPL {
    
    // Download and store all oeis multiplicative functions in db
    // downloadAndSaveOEIS()
    
    // Print simple bell tables for all multiplicative functions in db
    // DBBellTables()
    
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
