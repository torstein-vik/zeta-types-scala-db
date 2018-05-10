package io.github.torsteinvik.zetatypes.db.query

import io.github.torsteinvik.zetatypes.db._
import io.github.torsteinvik.zetatypes.db.query.Property._
import io.github.torsteinvik.zetatypes.db.Datatypes._

final class Requirements(requirements : Set[MFProperty[_]]) {
    
    type PropertyValue[T] = (MFProperty[T], T)
    
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
    
    // TODO: Account for case where minimal = Set()
    def createProvidersFromPointers(pointers : Seq[QueryPointer]) : Seq[MFPropertyProvider] = pointers.map { pointer => 
        def evalPropertyValue[T] (property : MFProperty[T]) : PropertyValue[property.output] = (property, pointer.evalMFProperty(property))
        
        val values : Seq[PropertyValue[_]] = minimal.toSeq.map(prop => evalPropertyValue[prop.output](prop.asInstanceOf[MFProperty[prop.output]]))
        
        values.map{case (property, value) => MFPropertyProvider(property)(value)}.reduce[MFPropertyProvider](_ ++ _)
        
    }
}
