package io.github.torsteinvik.zetatypes.db.query.parsing

import io.github.torsteinvik.zetatypes.db._
import io.github.torsteinvik.zetatypes.db.Datatypes._
import io.github.torsteinvik.zetatypes.db.query._

import scala.util.parsing.combinator._
import scala.util.parsing.input.{Position => ParsePosition}

import scala.reflect.runtime.universe._

object Parser extends RegexParsers {
    
    case class ParserException(msg : String, pos : ParsePosition) extends 
        Exception("Parsing error at line " + pos.line + " column " + pos.column + "\n" + pos.longString + "\n" + msg)
    
    def parse[T] (str : String)(implicit parser : Parser[T]) : T = parseAll(parser, str) match {
        case Success(result, _) => result
        case Failure(msg, next) => throw ParserException(msg, next.pos)
        case Error(msg, next) => throw ParserException(msg, next.pos)
    }
    
    object arguments {
        def single[T : TypeTag] : Parser[T] = "(" ~> literal[T] <~ ")"
        def double[T : TypeTag, S : TypeTag] : Parser[(T, S)] = "(" ~> (literal[T] <~ ",") ~ literal[S] <~ ")" ^^ { case t ~ s => (t, s) }
    }
    
    def property[T : TypeTag] : Parser[Property[T]] = mfproperty[T] | compoundproperty[T]
    def compoundproperty[T : TypeTag] : Parser[CompoundProperty[T]] = ???
    
    def mfproperty[T : TypeTag] : Parser[MFProperty[T]] = {
        import mfproperties._
        typeOf[T] match {
            case t if t =:= typeOf[MultiplicativeFunction] => mf
            case t if t =:= typeOf[String] => (mflabel | name | definition)
            case t if t =:= typeOf[Option[String]] => batchid
            case t if t =:= typeOf[Seq[String]] => comments
            case t if t =:= typeOf[Record[Boolean]] => properties
            case t if t =:= typeOf[Option[ComplexNumber]] => bellcell
            case t if t =:= typeOf[Option[Seq[ComplexNumber]]] => bellrow
            case t if t =:= typeOf[Seq[(Prime, Seq[ComplexNumber])]] => (belltable | bellsmalltable)
            case t => failure("There is no MFProperty of type " + t)
        }
    }.map(_.asInstanceOf[MFProperty[T]])
    
    object mfproperties {
        def mf          : Parser[MFProperty[MultiplicativeFunction]]             = "mf"          ^^^ Property.mf
        def batchid     : Parser[MFProperty[Option[String]]]                     = "batchid"     ^^^ Property.batchid
        def mflabel     : Parser[MFProperty[String]]                             = "mflabel"     ^^^ Property.mflabel
        def name        : Parser[MFProperty[String]]                             = "name"        ^^^ Property.name
        def definition  : Parser[MFProperty[String]]                             = "definition"  ^^^ Property.definition
        def comments    : Parser[MFProperty[Seq[String]]]                        = "comments"    ^^^ Property.comments
        def properties  : Parser[MFProperty[Record[Boolean]]]                    = "properties"  ^^^ Property.properties
        def belltable   : Parser[MFProperty[Seq[(Prime, Seq[ComplexNumber])]]]   = "belltable"   ^^^ Property.belltable
        
        def bellcell : Parser[MFProperty[Option[ComplexNumber]]] = "bellcell" ~> arguments.double[Prime, Nat] ^^ (Property.bellcell.tupled)
        def bellrow : Parser[MFProperty[Seq[ComplexNumber]]] = "bellrow" ~> arguments.single[Prime] ^^ (Property.bellrow(_))
        def bellsmalltable : Parser[MFProperty[Seq[(Prime, Seq[ComplexNumber])]]] = "bellsmalltable" ~> arguments.double[Int, Int] ^^ (Property.bellsmalltable.tupled)
        
    }
    
    def literal[T : TypeTag] : Parser[T] = {
        import literals._
        typeOf[T] match {
            case t if t =:= typeOf[Int] => int
            case t if t =:= typeOf[Integer] => integer
            case t if t =:= typeOf[Prime] => prime
            case t if t =:= typeOf[Nat] => natural
            case t if t =:= typeOf[String] => string
            case t => failure("There is no literal for type " + t)
        }
    }.map(_.asInstanceOf[T])
    
    object literals {
        private def bigint : Parser[BigInt] = """-?\d+""".r ^^ (BigInt(_))
        
        def int : Parser[Int] = """-?\d+""".r ^^ (_.toInt)
        
        def integer : Parser[Integer] = bigint ^^ (Integer(_))
        def prime : Parser[Prime] = bigint ^^ (Prime(_))
        def natural : Parser[Nat] = bigint ^^ (Nat(_))
        
        def string : Parser[String] = """(")(?:(?=(\\?))\2.)*?(\1)""".r ^^ (_.drop(1).dropRight(1)) ^^ (StringContext.treatEscapes(_))
    }
}
