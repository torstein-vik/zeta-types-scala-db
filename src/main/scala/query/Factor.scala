package io.github.torsteinvik.zetatypes.db.query

import io.github.torsteinvik.zetatypes.db.Datatypes._

import scala.annotation.tailrec

// TODO: This really needs to be placed somewhere more fitting...
object Factor {
    
    // TODO: unify with other primes and naturals stream
    //credit: https://gist.github.com/ramn/8378315
    private lazy val primes: Stream[Int] = 2 #:: Stream.from(3).filter { n => !primes.takeWhile(_ <= math.sqrt(n)).exists(n % _ == 0) }
    //credit: https://stackoverflow.com/questions/8566532/scala-streams-and-their-memory-usage
    private lazy val naturals: Stream[Int] = Stream.cons(0, naturals.map{_ + 1})
    
    // TODO: move this elsewhere...
    def apply(nat : Nat) : Seq[(Prime, Nat)] = {
        @tailrec
        def factor_(n : BigInt, curindex : Int, seq : Seq[(Int, Int)]) : Seq[(Int, Int)] = {
            if (n == BigInt(1)) return seq
            val (p : Int, i : Int) = primes.zipWithIndex.drop(curindex).find{n % _._1 == BigInt(0)}.get
            val e : Int = naturals.find(e => n % BigInt(p).pow(e) > 0).get - 1
            factor_(n / BigInt(p).pow(e), i + 1, (p, e) +: seq)
        }
        
        if(nat == Nat(1)) return Seq((Prime(2), Nat(0)))
        factor_(nat.x, 0, Seq()).map{case (p, e) => Prime(p) -> Nat(e)}
    }
}
