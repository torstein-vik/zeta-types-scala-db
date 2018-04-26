package io.github.torsteinvik.zetatypes.db

object Main extends App with REPL {
    
    // Download and store all oeis multiplicative functions in db
    // downloadAndSaveOEIS()
    
    // Print simple bell tables for all multiplicative functions in db
    // DBBellTables()
    
    def downloadAndSaveOEIS() {
        val multsInOEIS : Seq[MultiplicativeFunction] = {
            import oeis._
            
            val download = Download()
            val conv = download.map(Converter.apply)
            
            conv
        }
        
        multsInOEIS.map(mfstore)
        
        println(multsInOEIS)
    }
    
    def DBBellTables() {
        val all = mfgetall
        
        all foreach {mf => println(mf.bellTableText()); println()}
    }
    
}
