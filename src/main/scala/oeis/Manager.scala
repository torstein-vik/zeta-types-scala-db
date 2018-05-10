package io.github.torsteinvik.zetatypes.db.oeis

import scala.concurrent._
import ExecutionContext.Implicits.global

import collection.mutable.Queue

import util.Try

import org.json4s._

import io.github.torsteinvik.zetatypes.db._

object Manager {
    def apply(saver : MultiplicativeFunction => Unit, useBFile : Boolean = true) {
    }
    
}
