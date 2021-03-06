package io.github.torsteinvik.zetatypes.db.query

import io.github.torsteinvik.zetatypes.db._
import io.github.torsteinvik.zetatypes.db.dbmath._
import io.github.torsteinvik.zetatypes.db.datatypes._

final class MFPropertyProvider(val provider : PartialFunction[MFProperty[_], _]) {
    final def provide[T] (providee : MFProperty[T]) : T = if(isDefinedAt(providee)) provider(providee).asInstanceOf[T] else throw new Exception("Property requested but not in requirements! " + providee)
    final def isDefinedAt[T] (providee : MFProperty[T]) : Boolean = provider.isDefinedAt(providee)
    
    final def apply[T] (providee : MFProperty[T]) : T = provide(providee)
    
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
            case bellrow(p) => t.bellTable.values.find(_._1 == p).map(_._2).getOrElse(Seq())
            case bellsmalltable(ps, es) => t.bellTable.values.take(ps).map{case (p, vals) => (p, vals.take(es))}
        })
        
        case (`belltable`, t : Seq[(Prime, Seq[ComplexNumber])]) => new MFPropertyProvider ({
            case `belltable` => t
            case bellcell(p, Nat(e)) => t.find(_._1 == p).map(_._2.lift(e.toInt)).flatten
            case bellrow(p) => t.find(_._1 == p).map(_._2).getOrElse(Seq())
            case bellsmalltable(ps, es) => t.take(ps).map{case (p, vals) => (p, vals.take(es))}
        })
        
        case (bellrow(prime), t : Seq[ComplexNumber]) => new MFPropertyProvider ({
            case bellrow(p) if p == prime => t
            case bellcell(p, Nat(e)) if p == prime => t.lift(e.toInt)
        })
        
        case (bellsmalltable(primes, exponents), t : Seq[(Prime, Seq[ComplexNumber])]) => new MFPropertyProvider ({
            case bellsmalltable(ps, es) if ps == primes && es == exponents => t
            case bellsmalltable(ps, es) if ps <= primes && es <= exponents => t.take(ps).map{case (p, vals) => (p, vals.take(es))}
            case bellcell(p, Nat(e)) if e < exponents && Primes.indexOf(p) + 1 <= primes => t.find(_._1 == p).get._2.lift(e.toInt)
        })
        
        case _ => new MFPropertyProvider ({
            case x if provider == x => t
        })
    }
    
    def provides[T, S] (provider : MFProperty[T], providee : MFProperty[S]) : Boolean = (provider, providee) match {
        case (x, y) if x == y => true
        case (`mf`, _) => true
        case (`belltable`, _ : bellcell) => true
        case (`belltable`, _ : bellrow) => true
        case (`belltable`, _ : bellsmalltable) => true
        case (bellrow(x), bellcell(y, _)) if x == y => true
        case (bellsmalltable(ps1, es1), bellsmalltable(ps2, es2)) if ps2 <= ps1 && es2 <= es1 => true
        case (bellsmalltable(ps, es), bellcell(p, Nat(e))) if e < es && Primes.indexOf(p) + 1 <= ps => true
        case _ => false
    }
}
