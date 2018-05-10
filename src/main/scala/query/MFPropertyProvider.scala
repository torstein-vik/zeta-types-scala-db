package io.github.torsteinvik.zetatypes.db.query

import io.github.torsteinvik.zetatypes.db._
import io.github.torsteinvik.zetatypes.db.query.Property._
import io.github.torsteinvik.zetatypes.db.Datatypes._

final class MFPropertyProvider(val provider : PartialFunction[MFProperty[_], _]) {
    final def provide[T] (providee : MFProperty[T]) : T = if(provider.isDefinedAt(providee)) provider(providee).asInstanceOf[T] else throw new Exception("Property requested but not in requirements! " + providee)
    
    final def ++ (other : MFPropertyProvider) = new MFPropertyProvider(provider orElse other.provider)
}

object MFPropertyProvider {
    
    /*
    
    Relations:
    x -> x 
    mf -> everything
    belltable -> bellcell, bellrow, bellsmalltable
    bellrow -> bellcell if correct prime
    bellsmalltable -> bellsmalltable if dimensions smaller or equal + bellcell if exponent and prime is included 
    
    */
    
    // TODO factor out t so that we can use isDefinedAt... this requires re-doing some semantics. Also would speed things up.
    def apply[T] (provider : MFProperty[T])(t : T) : MFPropertyProvider = (provider, t) match {
        
    }
}
