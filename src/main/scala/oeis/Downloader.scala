package io.github.torsteinvik.zetatypes.db.oeis

import scala.io.Source

import scala.concurrent._
import ExecutionContext.Implicits.global

import collection.mutable.Queue

import util.Try

object Downloader {
    private val downloadQueue : Queue[(String, Promise[Seq[String]])] = Queue()
    
    def apply(url : String) : Future[Seq[String]] = {
        val promise = Promise[Seq[String]]()
        downloadQueue.enqueue((url, promise))
        promise.future
    }
    
}
