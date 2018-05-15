package io.github.torsteinvik.zetatypes.db

object Main extends App with REPL {
    
    import query.Property._
    
    // Download and store all oeis multiplicative functions in db, and use b-files for extra data
    // downloadAndSaveOEIS()
    
    def downloadAndSaveOEIS() = {
        import scala.concurrent.duration.Duration
        oeis.Manager(mfstore, useBFile = true, timeout = Duration.Inf)
    }
    
    // All multiplicative functions where 24 and 51 are fix-points
    // mfquery(pretty() where (mfvalue(24) ==? 24 and mfvalue(51) ==? 51)).print()
    
    // Close database
    close()
}
