package io.github.torsteinvik.zetatypes.db.query

import io.github.torsteinvik.zetatypes.db.dbmath._
import io.github.torsteinvik.zetatypes.db.datatypes._

abstract sealed class CompoundProperty[T](requirements : Set[MFProperty[_]]) extends Property[T](PropertySubtypeLock) {
    override def requires = requirements
    def apply (eval : Evaluator) : T
} 

case class ConstantProperty[T](value : T) extends CompoundProperty[T](Set()) {
    def apply (eval : Evaluator) : T = value
}

case class TupledProperties2[T, S](value1 : Property[T], value2 : Property[S]) extends CompoundProperty[(T, S)](value1.requires ++ value2.requires) {
    def apply (eval : Evaluator) : (T, S) = (eval(value1), eval(value2))
}

case class TupledProperties3[T, S, U](value1 : Property[T], value2 : Property[S], value3 : Property[U]) extends CompoundProperty[(T, S, U)](value1.requires ++ value2.requires ++ value3.requires) {
    def apply (eval : Evaluator) : (T, S, U) = (eval(value1), eval(value2), eval(value3))
}

case class TupledProperties4[T, S, U, V](value1 : Property[T], value2 : Property[S], value3 : Property[U], value4 : Property[V]) extends CompoundProperty[(T, S, U, V)](value1.requires ++ value2.requires ++ value3.requires ++ value4.requires) {
    def apply (eval : Evaluator) : (T, S, U, V) = (eval(value1), eval(value2), eval(value3), eval(value4))
}

case class TupledProperties5[T, S, U, V, W](value1 : Property[T], value2 : Property[S], value3 : Property[U], value4 : Property[V], value5 : Property[W]) extends CompoundProperty[(T, S, U, V, W)](value1.requires ++ value2.requires ++ value3.requires ++ value4.requires ++ value5.requires) {
    def apply (eval : Evaluator) : (T, S, U, V, W) = (eval(value1), eval(value2), eval(value3), eval(value4), eval(value5))
}

case class GetProperty[T](inner : Property[Option[T]]) extends CompoundProperty[T](inner.requires) {
    def apply (eval : Evaluator) : T = eval(inner) match {
        case Some(y) => y
        case None => throw new Exception("Value assumed to exist in query did not exist! Property: " + inner)
    }
}

case class ApplyProperty[T](inner : Property[Record[T]], name : String) extends CompoundProperty[Option[T]](inner.requires) {
    def apply (eval : Evaluator) : Option[T] = eval(inner).entries.find(_._1 == name).map(_._2)
}

case class TupleFirstProperty[T, S](inner : Property[(T, S)]) extends CompoundProperty[T](inner.requires) {
    def apply (eval : Evaluator) : T = eval(inner)._1
}

case class TupleSecondProperty[T, S](inner : Property[(T, S)]) extends CompoundProperty[S](inner.requires) {
    def apply (eval : Evaluator) : S = eval(inner)._2
}

case class mfpretty(ps : Int = 10, es : Int = 15) extends CompoundProperty[String](Set(bellsmalltable(ps, es), mflabel, name, definition)) {
    def apply (eval : Evaluator) : String = f"Label: ${eval(mflabel)} \t Name: ${eval(name)} \nDescription: ${eval(definition)} \n\nBell Table: \n" + (
        for {(Prime(prime), vals) <- eval(bellsmalltable(ps, es))} 
            yield f"p=${prime}: \t " + vals.map(_.pretty).mkString(", \t")).mkString("\n")
}

case class mfvalue(n : Nat) extends CompoundProperty[Option[ComplexNumber]](Factor(n).toSet.map(bellcell.tupled)) {
    val factors : Set[bellcell] = requires.asInstanceOf[Set[bellcell]]
    
    def apply (eval : Evaluator) : Option[ComplexNumber] = n match {
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
