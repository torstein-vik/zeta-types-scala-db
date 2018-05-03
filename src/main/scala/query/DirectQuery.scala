package io.github.torsteinvik.zetatypes.db.query

import io.github.torsteinvik.zetatypes.db._
import io.github.torsteinvik.zetatypes.db.query.Property._
import io.github.torsteinvik.zetatypes.db.Datatypes._

import scala.annotation.tailrec

object DirectQuery {
    
    def query[T](q : Query[T])(mfs : Seq[MultiplicativeFunction]) : QueryResult[T] = new QueryResult(q match {
        case q : PropertyQuery[T] => q match {
            case CombinedPropertyQuery(q1, q2) => (query(q1)(mfs) zip query(q2)(mfs)) map {case (x, y) => x ~ y}
            case SinglePropertyQuery(property) => mfs.map(evalProperty(property, _))
        }
        case FilteredQuery(innerq, predicate) => query(innerq)(mfs.filter(evalPredicate(predicate, _)))
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
            case Property.definition => mf.metadata.verbalDefinition
            case Property.comments => mf.metadata.comments
            case Property.properties => mf.properties.entries.collect{ case (property, true) => property }
            
            case belltable(ps, es) => mf.bellTableText(ps, es)
            case mfbell(p, Nat(e)) => mf.bellTable.values.find(_._1 == p).map(_._2.lift(e.toInt)).flatten
            case mfvalue(nn @ Nat(n)) => n match {
                case _ if n == 0 => Some(Nat(0)) 
                case _ if n == 1 => mf.bellTable.values.headOption.map(_._2.headOption).flatten // In all cases but one, this is 1. Discuss this counterexample, should it be included?
                case _ => {
                    val parts = factor(nn).map(mfbell.tupled).map(evalProperty[Option[ComplexNumber]](_, mf))
                    if (parts.exists(_ isEmpty)) None else Some(parts.map(_.get).foldLeft[ComplexNumber](Integer(1))({
                        case (acc, next) => CartesianComplex(Floating(acc.re * next.re - acc.im * next.im), Floating(acc.re * next.im + acc.im * next.re))
                    }))
                }
            }
        }
    }
    
    def evalPredicate(p : Predicate, mf : MultiplicativeFunction) : Boolean = p match {
        case EqualityPredicate(prop1, prop2) => evalProperty(prop1, mf) == evalProperty(prop2, mf)
        case StringContainsPredicate(superstr, substr) => evalProperty(superstr, mf) contains evalProperty(substr, mf)
        case RegexPredicate(str, regex) => evalProperty(str, mf) match {case regex() => true case _ => false}
        
        case SeqContainsPredicate(seq, element) => evalProperty(seq, mf) contains evalProperty(element, mf)
        case SeqHasPredicate(seq, pred) => evalProperty(seq, mf).exists(v => evalPredicate(pred(ConstantProperty(v)), mf))
        case SeqAllPredicate(seq, pred) => evalProperty(seq, mf).forall(v => evalPredicate(pred(ConstantProperty(v)), mf))
        
        case ExistsPredicate(opt) => !evalProperty(opt, mf).isEmpty
        
        case AndPredicate(pred1, pred2) => evalPredicate(pred1, mf) && evalPredicate(pred2, mf)
        case OrPredicate(pred1, pred2) => evalPredicate(pred1, mf) || evalPredicate(pred2, mf)
        case NotPredicate(pred) => !evalPredicate(pred, mf)
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
