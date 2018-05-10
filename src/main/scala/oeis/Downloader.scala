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
    
    
    private def runDownloader() : Unit = {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        while(true){
            Thread.sleep(300)
            //println("Queue size: " + downloadQueue.length) // For some reason this line is essential. What the fuck.
            if(!downloadQueue.isEmpty) handleRequest()
            if(!downloadQueue.isEmpty) handleRequest()
            if(!downloadQueue.isEmpty) handleRequest()
            if(!downloadQueue.isEmpty) handleRequest()
        }
    }
    
    private def handleRequest() : Unit = {
        val (url : String, promise : Promise[Seq[String]]) = /*if(downloadQueue.length > 200) downloadQueue.dequeueFirst(_._1) else*/ downloadQueue.dequeue()
        println("Downloading " + url)
        val source = Source.fromURL(url)("UTF-8") 
        val download = Try(source.getLines.toList) 
        source.close()
        promise.complete(download)
    }
}
