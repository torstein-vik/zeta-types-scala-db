package io.github.torsteinvik.zetatypes.db.oeis

import scala.io.Source

import scala.concurrent._
import ExecutionContext.Implicits.global

import collection.mutable.Queue

object Downloader {
    private val downloadQueue : Queue[Promise[Unit]] = Queue()
    
    def apply(url : String) : Future[Seq[String]] = {
        val promise = Promise[Unit]()
        downloadQueue.enqueue(promise)
        promise.future.map { _ => download(url) }
    }
    
    Future {runDownloader()}
    
    private def runDownloader() : Unit = {
        while(true){
            Thread.sleep(750)
            if(!downloadQueue.isEmpty) downloadQueue.dequeue().success(())
        }
    }
    
    private def download(url : String) : Seq[String] = {
        val source = Source.fromURL(url)("UTF-8") 
        try source.getLines.toList finally source.close()
    }
}
