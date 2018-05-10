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
        case (`mf`, t : MultiplicativeFunction) => new MFPropertyProvider ({
            case `mf` => t
            case `mflabel` => t.mflabel
            case `batchid` => t.metadata.batchId
            case `name` => t.metadata.descriptiveName
            case `definition` => t.metadata.verbalDefinition
            case `comments` => t.metadata.comments
            case `properties` => t.properties
            case `belltable` => t.bellTable.values
            case bellcell(p, Nat(e)) => t.bellTable.values.find(_._1 == p).map(_._2.lift(e.toInt)).flatten
            case bellrow(p) => t.bellTable.values.find(_._1 == p).map(_._2)
            case bellsmalltable(ps, es) => t.bellTable.values.take(ps).map{case (p, vals) => (p, vals.take(es))}
        })
        
        case (`belltable`, t : Seq[(Prime, Seq[ComplexNumber])]) => new MFPropertyProvider ({
            case `belltable` => t
            case bellcell(p, Nat(e)) => t.find(_._1 == p).map(_._2.lift(e.toInt)).flatten
            case bellrow(p) => t.find(_._1 == p).map(_._2)
            case bellsmalltable(ps, es) => t.take(ps).map{case (p, vals) => (p, vals.take(es))}
        })
        
        case (bellrow(prime), t : Option[Seq[ComplexNumber]]) => new MFPropertyProvider ({
            case bellrow(p) if p == prime => t
            case bellcell(p, Nat(e)) if p == prime => t.map(_.lift(e.toInt))
        })
        
        case (bellsmalltable(primes, exponents), t : Seq[(Prime, Seq[ComplexNumber])]) => new MFPropertyProvider ({
            case bellsmalltable(ps, es) if ps == primes && es == exponents => t
            case bellsmalltable(ps, es) if ps <= primes && es <= exponents => t.take(ps).map{case (p, vals) => (p, vals.take(es))}
            case bellcell(p, Nat(e)) if e < exponents && t.exists(_._1 == p) => t.find(_._1 == p).get._2.lift(e.toInt)
        })
        
        case _ => new MFPropertyProvider ({
            case x if provider == x => t
        })
    }
}
