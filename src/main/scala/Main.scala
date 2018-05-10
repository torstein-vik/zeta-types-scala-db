package io.github.torsteinvik.zetatypes.db

object Main extends App with REPL {
    
    import query.Property._
    
    /* TODO:
    1. Speed up belltable-generation in OEIS converter
       - Implement better prime sieve
       - Debug the other time-sinks
    
    2. Get the OEIS downloader working
       - There appear to be memory leaks
    
    3. Test out queries on the large OEIS db for debug and benchmark purposes
    
    4. Get the mongo property-provider to only encode the bell-table values that are needed, this is a huge time-sink
    
    5. Get the mongo query system to only retrive what is needed, using projections
    
    6. Finish parser
    
    7. Create a web-api
    
    8. Deploy on heroku
    
    9. Create a webpage
    
    10. Integrate with zeta-types-scala main project, add many mathematical tools to query system
    
    11. Create an LMFDB downloader
    
    12. Work on test-cases and making them as simple as possible
    
    */
    
    // Download and store all oeis multiplicative functions in db, and use b-files for extra data
    // oeis.Manager(mfstore, useBFile = true)
    
    // All multiplicative functions where 24 and 51 are fix-points
    // mfquery(pretty() where (mfvalue(24) ==? 24 and mfvalue(51) ==? 51)).print()
        
    // Close database
    close()
}
