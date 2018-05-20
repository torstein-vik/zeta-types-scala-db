package io.github.torsteinvik.zetatypes.db.dbmath

import io.github.torsteinvik.zetatypes.db.datatypes._

import scala.annotation.tailrec

object Factor {
    import Primes._
    
    def apply(nat : Nat) : Seq[(Prime, Nat)] = {
        require(nat != BigInt(0), "Can't factor zero into prime exponents!")
        
        @tailrec
        def factor_(n : BigInt, curindex : Int, seq : Seq[(Int, Int)]) : Seq[(Int, Int)] = {
            if (n == BigInt(1)) return seq
            val (p : Int, i : Int) = primesAsInt.zipWithIndex.drop(curindex).find{n % _._1 == BigInt(0)}.get
            val e : Int = Stream.from(0).find(e => n % BigInt(p).pow(e) > 0).get - 1
            factor_(n / BigInt(p).pow(e), i + 1, (p, e) +: seq)
        }
        
        factor_(nat.x, 0, Seq()).map{case (p, e) => new Prime(p) -> new Nat(e)}
    }
}
