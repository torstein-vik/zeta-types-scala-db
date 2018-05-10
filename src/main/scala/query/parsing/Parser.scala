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
    
}
