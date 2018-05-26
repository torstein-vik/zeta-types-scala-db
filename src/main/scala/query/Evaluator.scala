package io.github.torsteinvik.zetatypes.db.query

trait Evaluator {
    def apply[T] (p : Property[T]) : T
    def apply (p : Predicate) : Boolean
    def apply[T] (f : PropertyLambda[T], input : T) : Boolean
}

object StandardEvaluator {
    private sealed abstract class EvalContext
    private case object NoContext extends EvalContext
    private case class LambdaContext[T](t : T) extends EvalContext
}

final class StandardEvaluator (mf : MFPropertyProvider) extends Evaluator {
    import StandardEvaluator._
    
    def apply[T] (p : Property[T]) : T = evalProperty(p)(NoContext)
    def apply (p : Predicate) : Boolean = evalPredicate(p)(NoContext)
    def apply[T] (f : PropertyLambda[T], input : T) : Boolean = evalPredicate(f.output)(LambdaContext[T](input))
    
    private def evalProperty[T](p : Property[T])(implicit ctx : EvalContext) : T = p match {
        case p : MFProperty[T] => mf(p)
        case p : CompoundProperty[T] => p(this)
        
        case LambdaInputProperty() => ctx match {
            case LambdaContext(t) => t.asInstanceOf[T]
            case NoContext => throw new Exception("LambdaInputProperty not in lambda position!")
        }
    }
    
    private def evalPredicate(p : Predicate)(implicit ctx : EvalContext) : Boolean = p match {
        case TruePredicate => true
        
        case EqualityPredicate(prop1, prop2) => evalProperty(prop1) == evalProperty(prop2)
        case StringContainsPredicate(superstr, substr) => evalProperty(superstr) contains evalProperty(substr)
        case RegexPredicate(str, regex) => evalProperty(str) match {case regex() => true case _ => false}
        
        case SeqContainsPredicate(seq, element) => evalProperty(seq) contains evalProperty(element)
        case SeqHasPredicate(seq, pred) => evalProperty(seq).exists(apply(pred, _))
        case SeqAllPredicate(seq, pred) => evalProperty(seq).forall(apply(pred, _))
        
        case ExistsPredicate(opt) => !evalProperty(opt).isEmpty
        
        case AndPredicate(pred1, pred2) => evalPredicate(pred1) && evalPredicate(pred2)
        case OrPredicate(pred1, pred2) => evalPredicate(pred1) || evalPredicate(pred2)
        case NotPredicate(pred) => !evalPredicate(pred)
        
        case BooleanPredicate(prop) => evalProperty(prop)
    }    
}
