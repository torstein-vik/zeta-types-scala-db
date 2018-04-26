package io.github.torsteinvik.zetatypes.db.oeis

import io.github.torsteinvik.zetatypes.db._
import io.github.torsteinvik.zetatypes.db.Datatypes._

import org.json4s._

object Converter{
    private implicit val formats = DefaultFormats
    
    def apply(json : JObject) : MultiplicativeFunction = {
        val oeisID : String = "A%06d".format((json \ "number").extract[Int])
        val predata : Seq[BigInt] = (json \ "data").extract[String].split(",").map(BigInt(_))
        val offset : Int = (json \ "offset").extract[String].split(",")(0).toInt
        val name : String = (json \ "name").extract[String]
        val keywords : Seq[(String, Boolean)] = (json \ "keyword").extract[String].split(",").map(s => ("oeis_" + s) -> true)
        val comments : Seq[String] = (json \ "comment").extract[Seq[String]]
        val author : String = (json \ "author").extract[String]
        
        val data : Seq[BigInt] = offset match {
            case k if k < 0 => predata.drop( - k)
            case 0 => predata
            case 1 => BigInt(1) +: predata
            case 2 => BigInt(1) +: BigInt(1) +: predata
            case n => throw new Exception("Weird OEIS offset at " + oeisID + ": " + n)
        }
        
    }
}
