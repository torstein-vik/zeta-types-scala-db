package io.github.torsteinvik.zetatypes.db.query

import io.github.torsteinvik.zetatypes.db.query.Property._
import io.github.torsteinvik.zetatypes.db.datatypes._

object DirectQuery {
    
    private[DirectQuery] sealed abstract class EvalContext
    case object NoContext extends EvalContext
    case class LambdaContext[T](t : T) extends EvalContext
    
    def query[T](q : Query[T])(mfs : Seq[MFPropertyProvider]) : QueryResult[T] = new QueryResult(mfs.map(queryOne(q)(_)).flatten)
    
    def queryOne[T](q : Query[T])(mf : MFPropertyProvider) : Option[T] = {
        if (evalPredicate(q.filter, mf)(NoContext)) Some(evalProjection(q.projection, mf)) else None
    }
    
    def evalProjection[T](p : Projection[T], mf : MFPropertyProvider) : T = p match {
        case Projection.PCombined(p1, p2) => evalProjection(p1, mf) ~ evalProjection(p2, mf)
        case Projection.PSingle(property) => evalProperty(property, mf)(NoContext)
    }
    
    def evalProperty[T](p : Property[T], mf : MFPropertyProvider)(implicit ctx : EvalContext) : T = p match {
        case p : MFProperty[T] => mf(p)
        
        case p : CompoundProperty[T] => p match {
            case LambdaInputProperty() => ctx match {
                case LambdaContext(t) => t.asInstanceOf[T]
                case NoContext => throw new Exception("LambdaInputProperty not in lambda position!")
            }
            case ConstantProperty(x) => x
            case GetProperty(x) => evalProperty(x, mf) match {
                case Some(y) => y
                case None => throw new Exception("Value assumed to exist in query did not exist! Property: " + x)
            }
            case ApplyProperty(record, name) => evalProperty(record, mf).entries.find(_._1 == name).map(_._2)
            case TupleFirstProperty(tuple) => evalProperty(tuple, mf)._1
            case TupleSecondProperty(tuple) => evalProperty(tuple, mf)._2
            case mfpretty(ps, es) => {
                f"Label: ${mf(mflabel)} \t Name: ${mf(name)} \nDescription: ${mf(definition)} \n\nBell Table: \n" + (
                    for {(Prime(prime), vals) <- mf(bellsmalltable(ps, es))} 
                        yield f"p=${prime}: \t " + vals.map(_.pretty).mkString(", \t")).mkString("\n")
            }
            case nn @ mfvalue(Nat(n)) => n match {
                case _ if n == 0 => Some(new Nat(0)) 
                case _ if n == 1 => Some(new Nat(1))
                case _ => {
                    val parts = nn.factors.map(evalProperty(_, mf))
                    if (parts.exists(_.isEmpty)) None else Some(parts.map(_.get).foldLeft[ComplexNumber](Integer(1))({
                        case (acc, next) => CartesianComplex(Floating(acc.re * next.re - acc.im * next.im), Floating(acc.re * next.im + acc.im * next.re))
                    }))
                }
            }
        }
    }
    
    def evalPredicate(p : Predicate, mf : MFPropertyProvider)(implicit ctx : EvalContext) : Boolean = p match {
        case TruePredicate => true
        
        case EqualityPredicate(prop1, prop2) => evalProperty(prop1, mf) == evalProperty(prop2, mf)
        case StringContainsPredicate(superstr, substr) => evalProperty(superstr, mf) contains evalProperty(substr, mf)
        case RegexPredicate(str, regex) => evalProperty(str, mf) match {case regex() => true case _ => false}
        
        case SeqContainsPredicate(seq, element) => evalProperty(seq, mf) contains evalProperty(element, mf)
        case SeqHasPredicate(seq, pred) => evalProperty(seq, mf).exists(evalPropertyLambda(pred, _, mf))
        case SeqAllPredicate(seq, pred) => evalProperty(seq, mf).forall(evalPropertyLambda(pred, _, mf))
        
        case ExistsPredicate(opt) => !evalProperty(opt, mf).isEmpty
        
        case AndPredicate(pred1, pred2) => evalPredicate(pred1, mf) && evalPredicate(pred2, mf)
        case OrPredicate(pred1, pred2) => evalPredicate(pred1, mf) || evalPredicate(pred2, mf)
        case NotPredicate(pred) => !evalPredicate(pred, mf)
        
        case BooleanPredicate(prop) => evalProperty(prop, mf)
    }
    
    def evalPropertyLambda[T](lambda : PropertyLambda[T], v : T, mf : MFPropertyProvider) : Boolean = evalPredicate(lambda.output, mf)(LambdaContext[T](v))
    
}
