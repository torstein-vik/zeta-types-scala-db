package io.github.torsteinvik.zetatypes.db.oeis

import org.json4s._

import java.util.concurrent.atomic._
import java.util.concurrent.TimeUnit

import io.github.torsteinvik.zetatypes.db._

import scala.concurrent.duration.Duration

object Manager {
    def apply(saver : MultiplicativeFunction => Unit, useBFile : Boolean = true, timeout : Duration = Duration(10, TimeUnit.MINUTES)) {
        import scala.concurrent._
        import ExecutionContext.Implicits.global
        
        
        val (count, download : Seq[Future[Seq[JObject]]]) = Download()
        
        val promises = collection.mutable.ArrayBuffer.empty[Future[Unit]]
        
                
        val uploaded : AtomicInteger = new AtomicInteger()
        val conv : Seq[Future[Unit]] = download.map(_.map{now => 
            now.foreach{ ob => 
                promises += Converter.apply(ob, useBFile).map { mf => 
                    saver(mf)
                    val up : Int = uploaded.incrementAndGet
                    printf("upload: %d of %d - %2.2f %% - %s\n", up, promises.length, (up.toFloat / promises.length) * 100, mf.mflabel)
                }
            }
        })
        
        Await.result(Future.sequence(conv), Duration.Inf)
        println("Downloaded all basic information!")
        
        Await.result(Future.sequence(promises.toList), Duration.Inf)
        println("Converted and uploaded all!")
        
    }
    
}
