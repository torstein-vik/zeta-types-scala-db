package io.github.torsteinvik.zetatypes.db.query

import io.github.torsteinvik.zetatypes.db.dbmath._
import io.github.torsteinvik.zetatypes.db.datatypes._

abstract sealed class CompoundProperty[T](requirements : Set[MFProperty[_]]) extends Property[T](PropertySubtypeLock) {
    override def requires = requirements
    def apply (eval : PropertyEvaluator) : T
} 

case class ConstantProperty[T](value : T) extends CompoundProperty[T](Set()) {
    def apply (eval : PropertyEvaluator) : T = value
}

case class GetProperty[T](inner : Property[Option[T]]) extends CompoundProperty[T](inner.requires) {
    def apply (eval : PropertyEvaluator) : T = eval(inner) match {
        case Some(y) => y
        case None => throw new Exception("Value assumed to exist in query did not exist! Property: " + inner)
    }
}

case class ApplyProperty[T](inner : Property[Record[T]], name : String) extends CompoundProperty[Option[T]](inner.requires) {
    def apply (eval : PropertyEvaluator) : Option[T] = eval(inner).entries.find(_._1 == name).map(_._2)
}

case class TupleFirstProperty[T, S](inner : Property[(T, S)]) extends CompoundProperty[T](inner.requires) {
    def apply (eval : PropertyEvaluator) : T = eval(inner)._1
}

case class TupleSecondProperty[T, S](inner : Property[(T, S)]) extends CompoundProperty[S](inner.requires) {
    def apply (eval : PropertyEvaluator) : S = eval(inner)._2
}

case class mfpretty(ps : Int = 10, es : Int = 15) extends CompoundProperty[String](Set(bellsmalltable(ps, es), mflabel, name, definition)) {
    def apply (eval : PropertyEvaluator) : String = f"Label: ${eval(mflabel)} \t Name: ${eval(name)} \nDescription: ${eval(definition)} \n\nBell Table: \n" + (
        for {(Prime(prime), vals) <- eval(bellsmalltable(ps, es))} 
            yield f"p=${prime}: \t " + vals.map(_.pretty).mkString(", \t")).mkString("\n")
}

case class mfvalue(n : Nat) extends CompoundProperty[Option[ComplexNumber]](Factor(n).toSet.map(bellcell.tupled)) {
    val factors : Set[bellcell] = requires.asInstanceOf[Set[bellcell]]
    
    def apply (eval : PropertyEvaluator) : Option[ComplexNumber] = n match {
        case _ if n == 0 => Some(new Nat(0)) 
        case _ if n == 1 => Some(new Nat(1))
        case _ => {
            val parts = factors.map(eval(_))
            if (parts.exists(_.isEmpty)) None else Some(parts.map(_.get).foldLeft[ComplexNumber](Integer(1))({
                case (acc, next) => CartesianComplex(Floating(acc.re * next.re - acc.im * next.im), Floating(acc.re * next.im + acc.im * next.re))
            }))
        }
    }
}
