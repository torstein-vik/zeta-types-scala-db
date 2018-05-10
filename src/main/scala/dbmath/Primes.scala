package io.github.torsteinvik.zetatypes.db.dbmath

object Primes {
    // TODO: This is very slow, speed up
    lazy val primes: Stream[Int] = 2 #:: Stream.from(3, 2).filter { n => !primes.takeWhile(_ <= math.sqrt(n)).exists(n % _ == 0) }    
}
