package io.github.torsteinvik.zetatypes.db.oeis

import scala.concurrent.duration.Duration
import scala.concurrent._
import ExecutionContext.Implicits.global

import org.json4s._

object BFile {
    
    def apply (oeislabel : String) : Future[(Int, Seq[BigInt])] = query(oeislabel)
    
    private val oeisregex = """A(\d{6})""".r
    private val bfileregex = """(-?\d+) (-?\d+)""".r
    private val bfileregex4 = """(-?\d+) (-?\d+)""".r.unanchored // This is required because of A226193 which annoyingly is out of format
    private val bfileregex2 = """(-?\d+)  (-?\d+)""".r.unanchored // This is required because of A008454 which annoyingly is out of format
    private val bfileregex3 = """(-?\d+)\t(-?\d+)""".r // This is required because of A101113 which annoyingly is out of format
    
    private def queryurl(label : String) = label match { case oeisregex(numeric) => s"https://oeis.org/A${numeric}/b${numeric}.txt" }
    
    // offset and data
    private def query (label : String) : Future[(Int, Seq[BigInt])] = {
        Downloader(queryurl(label)).map{ result => 
            val data = result.filterNot(_.startsWith("#")).collect{ 
                case bfileregex(index, value) => (BigInt(index), BigInt(value)) 
                case bfileregex2(index, value) => (BigInt(index), BigInt(value)) 
                case bfileregex3(index, value) => (BigInt(index), BigInt(value)) 
                case bfileregex4(index, value) => (BigInt(index), BigInt(value)) 
            }
            if (data.length == 0) throw new Exception("Empty b-file! " + label)
            val offset = data(0)._1
            Future {if (data.zipWithIndex.exists{case ((input, _), index) => input - offset != index}) throw new Exception("Gap in b-file! There was assumed no gap so data is bad! Label: " + label)}
            
            (offset.toInt, data.map(_._2))
        }
    }
}
