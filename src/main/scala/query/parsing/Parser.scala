package io.github.torsteinvik.zetatypes.db.query.parsing

import io.github.torsteinvik.zetatypes.db._
import io.github.torsteinvik.zetatypes.db.Datatypes._
import io.github.torsteinvik.zetatypes.db.query._
import io.github.torsteinvik.zetatypes.db.query.Property._

import scala.util.parsing.combinator._
import scala.util.parsing.input._

import scala.reflect.runtime.universe.{Position => _, _}

object Parser extends RegexParsers {
    
    case class ParserException(msg : String, pos : Position) extends 
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
    
    def literal[T : TypeTag] : Parser[T] = (typeOf[T] match {
        case t if t =:= typeOf[Int] => literals.int
        case t if t =:= typeOf[Integer] => literals.integer
        case t if t =:= typeOf[Prime] => literals.prime
        case t if t =:= typeOf[Nat] => literals.natural
        case t if t =:= typeOf[String] => literals.string
        case _ => failure("Expected Literal")
    }).map(_.asInstanceOf[T])
    
    object literals {
        private def bigint : Parser[BigInt] = """-?\d+""".r ^^ (BigInt(_))
        
        def int : Parser[Int] = """-?\d+""".r ^^ (_.toInt)
        
        def integer : Parser[Integer] = bigint ^^ (Integer(_))
        def prime : Parser[Prime] = bigint ^^ (Prime(_))
        def natural : Parser[Nat] = bigint ^^ (Nat(_))
        
        def string : Parser[String] = """(")(?:(?=(\\?))\2.)*?(\1)""".r ^^ (_.drop(1).dropRight(1)) ^^ (StringContext.treatEscapes(_))
    }
}
