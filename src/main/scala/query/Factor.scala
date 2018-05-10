package io.github.torsteinvik.zetatypes.db.query

import io.github.torsteinvik.zetatypes.db.Datatypes._
import io.github.torsteinvik.zetatypes.db.dbmath.Primes._

import scala.annotation.tailrec

// TODO: This really needs to be placed somewhere more fitting...
object Factor {
    
    def apply(nat : Nat) : Seq[(Prime, Nat)] = {
        @tailrec
        def factor_(n : BigInt, curindex : Int, seq : Seq[(Int, Int)]) : Seq[(Int, Int)] = {
            if (n == BigInt(1)) return seq
            val (p : Int, i : Int) = primes.zipWithIndex.drop(curindex).find{n % _._1 == BigInt(0)}.get
            val e : Int = Stream.from(0).find(e => n % BigInt(p).pow(e) > 0).get - 1
            factor_(n / BigInt(p).pow(e), i + 1, (p, e) +: seq)
        }
        
        if(nat == Nat(1)) return Seq((Prime(2), Nat(0))) // Should this be included???? surely Seq() will do
        factor_(nat.x, 0, Seq()).map{case (p, e) => Prime(p) -> Nat(e)}
    }
}
