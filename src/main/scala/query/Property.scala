package io.github.torsteinvik.zetatypes.db.query

import io.github.torsteinvik.zetatypes.db._
import io.github.torsteinvik.zetatypes.db.codec._
import io.github.torsteinvik.zetatypes.db.dbmath._
import io.github.torsteinvik.zetatypes.db.datatypes._

import scala.util.matching.Regex

abstract sealed class Property[T] {
    final type output = T
    
    def requires : Set[MFProperty[_]]
    
    final def === (other : Property[T]) : Predicate = EqualityPredicate[T](this, other)
    final def !== (other : Property[T]) : Predicate = EqualityPredicate[T](this, other).not
}

// TODO: move

trait PropertyEvaluator {
    def apply[T] (p : Property[T]) : T
}

abstract sealed class MFProperty[T] extends Property[T] {def requires = Set(this)}
abstract sealed class JSONProperty[T] (val path : String*)(implicit val codec : Codec[T]) extends MFProperty[T]
abstract sealed class CompoundProperty[T](requirements : Set[MFProperty[_]]) extends Property[T] {
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

case class PropertyLambda[T](output : Predicate)
case class LambdaInputProperty[T]() extends Property[T] {def requires = Set()}

object Property {
    import scala.language.implicitConversions
    implicit def liftProperty[S, T](s : S)(implicit f : S => T) : ConstantProperty[T] = ConstantProperty[T](f(s))
    implicit def liftPropertyLambda[T](f : Property[T] => Predicate) : PropertyLambda[T] = PropertyLambda[T](f(LambdaInputProperty[T]()))
    
    case object mf extends JSONProperty[MultiplicativeFunction]()
    case object mflabel extends JSONProperty[String]("mflabel")
    case object batchid extends JSONProperty[Option[String]]("metadata", "batchId")
    case object name extends JSONProperty[String]("metadata", "descriptiveName")
    case object definition extends JSONProperty[String]("metadata", "verbalDefinition")
    case object comments extends JSONProperty[Seq[String]]("metadata", "comments")
    case object properties extends JSONProperty[Record[Boolean]]("properties")
    case object belltable extends JSONProperty[Seq[(Prime, Seq[ComplexNumber])]]("bellTable", "values")
    
    case class bellcell(p : Prime, e : Nat) extends MFProperty[Option[ComplexNumber]]
    case class bellrow(p : Prime) extends MFProperty[Seq[ComplexNumber]]
    case class bellsmalltable(ps : Int = 10, es : Int = 15) extends MFProperty[Seq[(Prime, Seq[ComplexNumber])]]
    
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
    
    implicit final class StringProperty(prop : Property[String]) {
        def contains (contains : Property[String]) : Predicate = StringContainsPredicate(prop, contains)
        def matches (regex : Regex) : Predicate = RegexPredicate(prop, regex)
    }
    
    implicit final class OptionProperty[T](prop : Property[Option[T]]) {
        def get = GetProperty(prop)
        
        def exists = ExistsPredicate(prop)
        final def ==? (other : Property[T]) : Predicate = exists and EqualityPredicate[T](get, other)
        final def !=? (other : Property[T]) : Predicate = exists and EqualityPredicate[T](get, other).not
    }

    implicit final class SeqProperty[T](prop : Property[Seq[T]]) {
        def contains (contains : Property[T]) : Predicate = SeqContainsPredicate(prop, contains)
        def has (pred : Property[T] => Predicate) : Predicate = SeqHasPredicate(prop, pred)
        def all (pred : Property[T] => Predicate) : Predicate = SeqAllPredicate(prop, pred)
    }

    implicit final class RecordProperty[T](prop : Property[Record[T]]) {
        def apply (name : String) : Property[Option[T]] = ApplyProperty(prop, name)
    }
    
    implicit final class TupleProperty[T, S](prop : Property[(T, S)]) {
        def _1 : Property[T] = TupleFirstProperty(prop)
        def _2 : Property[S] = TupleSecondProperty(prop)
    }
    
    implicit def toProjection[T](p : Property[T]) : Projection[T] = Projection(p)
    implicit def toQuery[T](p : Property[T]) : Query[T] = Query(Projection(p))
}

object JSONProperty {
    def unapply[T] (p : JSONProperty[T]) : Option[Seq[String]] = p match {case p : JSONProperty[_] => Some(p.path) case _ => None}
    def unapplySeq[T] (p : JSONProperty[T]) : Option[Seq[String]] = unapply(p)
}
