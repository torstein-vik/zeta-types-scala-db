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
            import scala.concurrent.duration.Duration
            import scala.concurrent._
            import ExecutionContext.Implicits.global
            
            // Download json from oeis.org
            println("Downloading...")
            val download = Download()
            // Convert json into list of MultiplicativeFunction
            println("Converting...")
            var converted : Int = 0
            val conv = download.zipWithIndex.map{ case (future, index) => 
                future.flatMap{ d => Future {
                        val mf = Converter.apply(d, true)
                        converted += 1
                        printf("conversion: %d of %d - %2.2f %%\n", converted, download.length, (converted.toFloat / download.length) * 100)
                        mf
                    }
                }
            }
            
            // Return this as multsInOEIS
            Await.result(Future.sequence(conv), Duration.Inf)
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
