package io.github.torsteinvik.zetatypes.db.oeis

import org.json4s._

import io.github.torsteinvik.zetatypes.db._

object Manager {
    def apply(saver : MultiplicativeFunction => Unit, useBFile : Boolean = true) {
        import scala.concurrent.duration.Duration
        import scala.concurrent._
        import ExecutionContext.Implicits.global
        
        
        val (count, download : Seq[Future[Seq[JObject]]]) = Download()
        
        val promises = collection.mutable.ArrayBuffer.empty[Future[Unit]]
        
        var uploaded : Int = 0
        val conv : Seq[Future[Unit]] = download.map(_.map{now => 
            now.foreach{ ob => 
                promises += Converter.apply(ob, useBFile).map { mf => 
                    saver(mf)
                    uploaded += 1
                    printf("upload: %d of %d - %2.2f %% - %s\n", uploaded, promises.length, (uploaded.toFloat / promises.length) * 100, mf.mflabel)
                }
            }
        })
        
        Await.result(Future.sequence(conv), Duration.Inf)
        println("Downloaded all basic information!")
        
        Await.result(Future.sequence(promises.toList), Duration.Inf)
        println("Converted and uploaded all!")
        
    }
    
}
