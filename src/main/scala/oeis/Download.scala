package io.github.torsteinvik.zetatypes.db.oeis

import scala.io.Source

import org.json4s._

object Download {
    private implicit val formats = DefaultFormats
    
    def apply() : Seq[JObject] = {
        
        val data = collection.mutable.ListBuffer[JObject]()
        val first = query(0)
        
        val count = (first \ "count").extract[Int]
        val amt = math.ceil(count.toFloat / 10).toInt
        
        println("count: " + count)
        println("queries: " + amt)
        
        for (i <- 0 to (amt - 1)) {
            val results = query(i)
            

            for ( result <- (results \ "results").extract[List[JObject]] ) {
                data += result
            }
            
            printf("%d of %d - %2.2f %%\n", math.min((i + 1) * 10, count), count, (math.min((i + 1) * 10, count).toFloat / count) * 100)
            
            Thread.sleep(500)
        }
                
        return data.to[Seq]
        
    }
    
    def queryurl(i : Int) = "https://oeis.org/search?q=keyword:mult&fmt=json&start=" + (i * 10)
    
    def query (i : Int) : JValue = {
        val source = Source.fromURL(queryurl(i))("UTF-8")
        val result = try source.mkString finally source.close
        
        import org.json4s.native.JsonMethods._
        return parse(result)
    }
}
