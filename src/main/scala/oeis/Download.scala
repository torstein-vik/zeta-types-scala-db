package io.github.torsteinvik.zetatypes.db.oeis

import org.json4s._

import scala.concurrent.duration.Duration
import scala.concurrent._
import ExecutionContext.Implicits.global


object Download {
    private implicit val formats = DefaultFormats
    
    def apply() : (Int, Seq[Future[Seq[JObject]]]) = {
        
        val first = Await.result(query(0), Duration.Inf)
        
        val count = (first \ "count").extract[Int]
        val amt = math.ceil(count.toFloat / 10).toInt
        
        println("count: " + count)
        println("queries: " + amt)
        
        var downloaded : Int = 0
        
        val data : Seq[Future[Seq[JObject]]] = for (i <- 0 to (amt - 1)) yield { query(i).map{ results => 
            val mfs = (results \ "results").extract[List[JObject]]

            downloaded += mfs.length
            printf("download: %d of %d - %2.2f %%\n", downloaded, count, (downloaded.toFloat / count) * 100)
            mfs
        }}
        
        return (count, data)
        
    }
    
    def queryurl(i : Int) = "https://oeis.org/search?q=keyword:mult&fmt=json&start=" + (i * 10)
    
    def query (i : Int) : Future[JValue] = {
        val result = Downloader(queryurl(i))
        
        import org.json4s.native.JsonMethods._
        return result.map(res => parse(res mkString "\n"))
    }
}
