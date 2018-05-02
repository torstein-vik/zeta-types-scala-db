package io.github.torsteinvik.zetatypes.db.query

import io.github.torsteinvik.zetatypes.db._

object DirectQuery {
    
    def query[T](q : Query[T])(mfs : Seq[MultiplicativeFunction]) : QueryResult[T] = new QueryResult(q match {
    })
    
    def evalProperty[T](p : Property[T], mf : MultiplicativeFunction) : T = p match {
        case ConstantProperty(x) => x
        case GetProperty(x) => evalProperty[Option[T]](x, mf) match {
            case Some(y) => y
            case None => throw new Exception("Value assumed to exist in q did not exist!")
        }
        case p : MFProperty[T] => p match {
            case Property.mf => mf
            case Property.mflabel => mf.mflabel
            case Property.batchid => mf.metadata.batchId
            case Property.name => mf.metadata.descriptiveName
            case Property.belltable => mf.bellTableText
            case Property.definition => mf.metadata.verbalDefinition
            case Property.comments => mf.metadata.comments
            case Property.properties => mf.properties.entries.collect{ case (property, true) => property }
            
            case mfbell(p, Nat(e)) => mf.bellTable.values.find(_._1 == p).map(_._2.lift(e.toInt)).flatten
        }
    }
    
    def evalPredicate(p : Predicate, mf : MultiplicativeFunction) : Boolean = p match {
    }
    
    //TODO: unify with other primes and naturals stream
    //credit: https://gist.github.com/ramn/8378315
    private lazy val primes: Stream[Int] = 2 #:: Stream.from(3).filter { n => !primes.takeWhile(_ <= math.sqrt(n)).exists(n % _ == 0) }
    //credit: https://stackoverflow.com/questions/8566532/scala-streams-and-their-memory-usage
    private lazy val naturals: Stream[Int] = Stream.cons(0, naturals.map{_ + 1})
    
    // TODO: move this elsewhere...
    private def factor(nat : Nat) : Seq[(Prime, Nat)] = {
        @tailrec
        def factor_(n : BigInt, curindex : Int, seq : Seq[(Int, Int)]) : Seq[(Int, Int)] = {
            if (n == BigInt(1)) return seq
            val (p : Int, i : Int) = primes.zipWithIndex.drop(curindex).find{n % _._1 == BigInt(0)}.get
            val e : Int = naturals.find(e => n % BigInt(p).pow(e) > 0).get - 1
            factor_(n / BigInt(p).pow(e), i + 1, (p, e) +: seq)
        }
        
        factor_(nat.x, 0, Seq()).map{case (p, e) => Prime(p) -> Nat(e)}
    }
    
}
