package io.github.torsteinvik.zetatypes.db.query.parsing

import io.github.torsteinvik.zetatypes.db._
import io.github.torsteinvik.zetatypes.db.Datatypes._
import io.github.torsteinvik.zetatypes.db.query._

import scala.reflect.runtime._
import scala.reflect.runtime.universe._
import scala.tools.reflect.ToolBox

import scala.util.{Try, Success, Failure}

object Parser {
    case class ParserException(msg : String) extends Exception(msg)
    
    private val toolBox = universe.runtimeMirror(getClass.getClassLoader).mkToolBox()
    
    def parse (query : String) : Query[_] = parseQuery(query).get
    
    private def parseQuery (query : String) : Try[Query[_]] = {
        val tree : universe.Tree = toolBox.parse(query)
        
        
        return Failure(ParserException("Not yet implemented"))
    }
    
}
