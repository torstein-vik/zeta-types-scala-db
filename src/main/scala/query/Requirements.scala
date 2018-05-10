package io.github.torsteinvik.zetatypes.db.query


final class Requirements(requirements : Set[MFProperty[_]]) {
    
    
    /*
    
    Relations:
    x -> x 
    mf -> everything
    belltable -> bellcell, bellrow, bellsmalltable
    bellrow -> bellcell if correct prime
    bellsmalltable -> bellsmalltable if dimensions smaller or equal + bellcell if exponent and prime is included 
    
    The latter two not implemented here
    
    */
    
}
