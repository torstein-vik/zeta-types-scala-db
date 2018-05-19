package io.github.torsteinvik.zetatypes.db.dbmath

import math._
import io.github.torsteinvik.zetatypes.db.Datatypes._

object Primes {

    // sieve, bound
    private var currentSieve : (Seq[Prime], Int) = (Seq.empty[Prime], 0)
    
    val primes: Stream[Prime] = Stream.from(0).map(getPrime)
    val primesAsInt: Stream[Int] = primes.map(_.toInt)

    def getPrime(index : Int) : Prime = currentSieve._1.applyOrElse(index, { i : Int => getPrimes(10000 * ceil((i + 1) / 10000.0).toInt)(i)})

    def indexOf(prime : Prime) : Int = primes.takeWhile(_ <= prime).indexOf(prime) match {
        case -1 => throw new Exception(f"Can't find index of $prime among primes, is it proven prime?")
        case n => n
    }
    
    def getPrimes(amount : Int) : Seq[Prime] = sieveToBound(primeSizeBound(amount)).take(amount)
    
    def getPrimesBounded(bound : Int) : Seq[Prime] = {
        if(bound <= currentSieve._2) currentSieve._1.takeWhile(_ < bound) else sieveToBound(bound)
    }

    private def primeSizeBound(amount : Int) : Int = amount match {
        case n if n < 5 => 16
        case n => ceil(n * log(n) + (n) * log(log(n))).toInt
    }
    
    private def sieveToBound(bound : Int) : Seq[Prime] = {
        if (bound <= 2) return Seq()
        
        val primes = collection.mutable.Buffer[Prime]()
    
        val primebound : Int = ceil(sqrt(bound)).toInt
        val isPrime : collection.mutable.IndexedSeq[Boolean] = collection.mutable.IndexedSeq.fill(bound)(true)
        
        for (p <- 2 to bound - 1 if isPrime(p)) {
            primes += new Prime(p)
            if (p < primebound) for (i <- Stream.range(p * p, bound, p)) isPrime.update(i, false)
        }
        
        currentSieve = (primes, bound)
        return primes.toSeq
    }
}
