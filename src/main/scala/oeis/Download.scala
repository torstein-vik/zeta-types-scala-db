package io.github.torsteinvik.zetatypes.db.oeis

import scala.io.Source

import org.json4s._

import scala.concurrent._
import ExecutionContext.Implicits.global


object Download {
    private implicit val formats = DefaultFormats
    
    def apply() : Seq[Future[JObject]] = {
        
        val first = query(0)
        
        val count = (first \ "count").extract[Int]
        val amt = math.ceil(count.toFloat / 10).toInt
        
        println("count: " + count)
        println("queries: " + amt)
        
        var downloaded : Int = 0
        
        val data : Seq[Promise[JObject]] = Seq.fill(count)(Promise[JObject]())
        
        for (i <- 0 to (amt - 1)) { Future {
            val results = query(i)
            val mfs = (results \ "results").extract[List[JObject]]

            for ( (mf, index) <- mfs.zipWithIndex ) {
                data(index + 10 * i).success(mf)
            }
            
            downloaded += mfs.length
            printf("download: %d of %d - %2.2f %%\n", downloaded, count, (downloaded.toFloat / count) * 100)
            
        }}
        
        return data.map(_.future)
        
    }
    
    def queryurl(i : Int) = "https://oeis.org/search?q=keyword:mult&fmt=json&start=" + (i * 10)
    
    def query (i : Int) : Future[JValue] = {
        val result = Downloader(queryurl(i))
        
        import org.json4s.native.JsonMethods._
        return result.map(res => parse(res mkString "\n"))
    }
}
