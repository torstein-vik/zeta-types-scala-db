package io.github.torsteinvik.zetatypes.db.query

import io.github.torsteinvik.zetatypes.db._
import io.github.torsteinvik.zetatypes.db.query.Property._
import io.github.torsteinvik.zetatypes.db.Datatypes._

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
    
    // TODO: Use isDefinedAt to do this automatically
    val minimal : Set[MFProperty[_]] = requirements match {
        case reqs if reqs.contains(mf) => Set(mf)
        case reqs if reqs.contains(belltable) => reqs.filter({
            case _ : bellcell => false
            case _ : bellrow => false
            case _ : bellsmalltable => false
            case _ => true
        })
        case reqs => reqs
    }
    
}
