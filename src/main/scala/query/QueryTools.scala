package io.github.torsteinvik.zetatypes.db.query

object QueryTools {
    
    private[QueryTools] sealed abstract class EvalContext
    private[QueryTools] case object NoContext extends EvalContext
    private[QueryTools] case class LambdaContext[T](t : T) extends EvalContext
    
    def query[T](q : Query[T])(mfs : Seq[MFPropertyProvider]) : QueryResult[T] = aggregate(q)(new QueryResult(mfs.map(filterAndProjectOne(q)(_)).flatten))
    
    def aggregate[T](q : Query[T])(mfs : QueryResult[T]) : QueryResult[T] = {
        
        new QueryResult(mfs.take(q.limit.getOrElse(mfs.length)))
    }
    
    def filterAndProjectOne[T](q : Query[T])(mf : MFPropertyProvider) : Option[T] = {
        if (evalPredicate(q.filter, mf)(NoContext)) Some(evalProperty(q.projection, mf)(NoContext)) else None
    }
        
    class StandardEvaluator (mf : MFPropertyProvider, ctx : EvalContext) extends Evaluator {
        def apply[T] (p : Property[T]) : T = evalProperty(p, mf)(ctx)
    }
    
    def evalProperty[T](p : Property[T], mf : MFPropertyProvider)(implicit ctx : EvalContext) : T = p match {
        case p : MFProperty[T] => mf(p)
        case p : CompoundProperty[T] => p(new Evaluator(mf, ctx))
        
        case LambdaInputProperty() => ctx match {
            case LambdaContext(t) => t.asInstanceOf[T]
            case NoContext => throw new Exception("LambdaInputProperty not in lambda position!")
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
