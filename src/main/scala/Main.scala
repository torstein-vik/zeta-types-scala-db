package io.github.torsteinvik.zetatypes.db

object Main extends App with REPL {
        
    // Download and store all oeis multiplicative functions in db, and use b-files for extra data
    // downloadAndSaveOEIS()
    
    def downloadAndSaveOEIS() = {
        import scala.concurrent.duration.Duration
        oeis.Manager(mfstore, useBFile = true, timeout = Duration.Inf)
    }
    
    // Query examples: 
    // import query.Property._
    
    // All multiplicative functions where 24 and 51 are fix-points
    // mfquery(mfpretty() where (mfvalue(24) ==? 24 and mfvalue(51) ==? 51)).print()
    
    // Query for a single multiplicative function
    // mfquery(mf where (mflabel === "MF-OEIS-A000005")).print()
    
    // All multiplicative functions which are marked as nice in OEIS
    // mfquery(mfpretty() where properties("oeis_nice")).print()
    
    // Close database
    Thread.sleep(200); close()
}
