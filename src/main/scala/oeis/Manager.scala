package io.github.torsteinvik.zetatypes.db.oeis

import org.json4s._

import java.util.concurrent.atomic._
import java.util.concurrent.TimeUnit

import io.github.torsteinvik.zetatypes.db._

import scala.concurrent.duration.Duration

import scala.util._

object Manager {
    def apply(saver : MultiplicativeFunction => Unit, useBFile : Boolean = true, timeout : Duration = Duration(10, TimeUnit.MINUTES)) {
        import scala.concurrent._
        import ExecutionContext.Implicits.global
        
        
        val (count, download : Seq[Future[Seq[JObject]]]) = Download()
                
        val uploaded : AtomicInteger = new AtomicInteger()
        val conv : Future[Seq[Try[Unit]]] = Future.sequence(download.map(_.map{x => Future.sequence(x.map{ fmf => 
                Converter.apply(fmf, useBFile).map { mf => 
                    saver(mf)
                    val up : Int = uploaded.incrementAndGet
                    printf("upload: %d of %d - %2.2f %% - %s\n", up, count, (up.toFloat / count) * 100, mf.mflabel)
                }.map(Success(_)).recover{case ce : ConversionException => Failure(ce)}
            })
        }.flatten)).map(_.flatten)
        
        
        val res = Await.result(conv, timeout)
        val excluded : Seq[(String, String)] = res.collect{case Failure(ConversionException(oeisID, msg)) => (oeisID, msg)}
        println()
        println("Finished downloading, converting, and uploading! Excluded " + excluded.length)
        excluded.foreach { case (oeisID, msg) => 
            println(oeisID + " was excluded because: " + msg)
        }
        println()
    }
    
}
