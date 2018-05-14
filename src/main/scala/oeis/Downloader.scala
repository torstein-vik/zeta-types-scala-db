package io.github.torsteinvik.zetatypes.db.oeis

import scala.io.Source

import scala.concurrent._
import ExecutionContext.Implicits.global

import collection.mutable.Queue

import util.Try

object Downloader {
    private val downloadQueue : Queue[Promise[Unit]] = Queue()
    
    def apply(url : String) : Future[Seq[String]] = {
        val promise = Promise[Unit]()
        downloadQueue.enqueue(promise)
        promise.future
    }
    
    Future {runDownloader()}
    Future {runDownloader()}
    
    private def runDownloader() : Unit = {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        while(true){
            Thread.sleep(500)
            if(!downloadQueue.isEmpty) downloadQueue.dequeue().success()
        }
    }
    
}
