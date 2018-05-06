package io.github.torsteinvik.zetatypes.db.query

import io.github.torsteinvik.zetatypes.db._
import io.github.torsteinvik.zetatypes.db.query.Property._
import io.github.torsteinvik.zetatypes.db.Datatypes._


object DirectQuery {
    
    private[DirectQuery] sealed abstract class EvalContext
    case object NoContext extends EvalContext
    case class LambdaContext[T](t : T) extends EvalContext
    
    def query[T](q : Query[T])(mfs : Seq[MultiplicativeFunction]) : QueryResult[T] = new QueryResult(q match {
        case q : PropertyQuery[T] => q match {
            case CombinedPropertyQuery(q1, q2) => (query(q1)(mfs) zip query(q2)(mfs)) map {case (x, y) => x ~ y}
            case SinglePropertyQuery(property) => mfs.map(evalProperty(property, _)(NoContext))
        }
        case FilteredQuery(innerq, predicate) => query(innerq)(mfs.filter(evalPredicate(predicate, _)(NoContext)))
    })
    
    def evalProperty[T](p : Property[T], mf : MultiplicativeFunction)(implicit ctx : EvalContext) : T = p match {
        case ConstantProperty(x) => x
        case GetProperty(x) => evalProperty[Option[T]](x, mf) match {
            case Some(y) => y
            case None => throw new Exception("Value assumed to exist in query did not exist!")
        }
        case p : LambdaInputProperty[T] => ctx match {
            case LambdaContext(t : T) => t
            case NoContext => throw new Exception("LambdaInputProperty not in lambda position!")
        }
        case p : MFProperty[T] => p match {
            case Property.mf => mf
            case Property.mflabel => mf.mflabel
            case Property.batchid => mf.metadata.batchId
            case Property.name => mf.metadata.descriptiveName
            case Property.definition => mf.metadata.verbalDefinition
            case Property.comments => mf.metadata.comments
            case Property.properties => mf.properties
            
            case bellcell(p, Nat(e)) => mf.bellTable.values.find(_._1 == p).map(_._2.lift(e.toInt)).flatten
            case pretty(ps, es) => mf.bellTableText(ps, es)
            case nn @ mfvalue(Nat(n)) => n match {
                case _ if n == 0 => Some(Nat(0)) 
                case _ if n == 1 => mf.bellTable.values.headOption.map(_._2.headOption).flatten // In all cases but one, this is 1. Discuss this counterexample, should it be included?
                case _ => {
                    val parts = nn.factors.map(evalProperty(_, mf))
                    if (parts.exists(_ isEmpty)) None else Some(parts.map(_.get).foldLeft[ComplexNumber](Integer(1))({
                        case (acc, next) => CartesianComplex(Floating(acc.re * next.re - acc.im * next.im), Floating(acc.re * next.im + acc.im * next.re))
                    }))
                }
            }
        }
    }
    
    def evalPredicate(p : Predicate, mf : MultiplicativeFunction)(implicit ctx : EvalContext) : Boolean = p match {
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
    
    def evalPropertyLambda[T](lambda : PropertyLambda[T], v : T, mf : MultiplicativeFunction) : Boolean = evalPredicate(lambda.output, mf)(LambdaContext[T](v))
    
}
