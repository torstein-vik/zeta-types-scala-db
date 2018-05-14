package io.github.torsteinvik.zetatypes.db.dbmath

import math._

object Primes {

    // sieve, bound
    private var currentSieve : (Seq[Int], Int) = (Seq.empty[Int], 0)
    
    val primes: Stream[Int] = Stream.from(0).map(getPrime)

    def getPrime(index : Int) : Int = currentSieve._1.applyOrElse(index, { i : Int => getPrimes(10000 * ceil((i + 1) / 10000.0).toInt)(i)})

    def getPrimes(amount : Int) : Seq[Int] = sieveToBound(primeSizeBound(amount)).take(amount)
    
    def getPrimesBounded(bound : Int) : Seq[Int] = {
        if(bound <= currentSieve._2) currentSieve._1.takeWhile(_ < bound) else sieveToBound(bound)
    }

    private def primeSizeBound(amount : Int) : Int = amount match {
        case n if n < 5 => 16
        case n => ceil(n * log(n) + (n) * log(log(n))).toInt
    }
    
    private def sieveToBound(bound : Int) : Seq[Int] = {
        if (bound <= 2) return Seq()
        
        val primes = collection.mutable.Buffer[Int]()
    
        val primebound : Int = ceil(sqrt(bound)).toInt
        val isPrime = collection.mutable.Seq.fill(bound)(true)
        
        for (p <- 2 to bound - 1 if isPrime(p)) {
            primes += p
            if (p < primebound) for (i <- Stream.range(p * p, bound, p)) isPrime.update(i, false)
        }
        
        currentSieve = (primes, bound)
        return primes.toSeq
    }
}
