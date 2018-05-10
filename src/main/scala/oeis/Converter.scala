package io.github.torsteinvik.zetatypes.db.oeis

import io.github.torsteinvik.zetatypes.db._
import io.github.torsteinvik.zetatypes.db.dbmath.Primes._
import io.github.torsteinvik.zetatypes.db.Datatypes._

import org.json4s._

import scala.concurrent._
import ExecutionContext.Implicits.global

object Converter{
    private implicit val formats = DefaultFormats
    def apply(json : JObject, useBFile : Boolean = false) : MultiplicativeFunction = {
        val oeisID : String = "A%06d".format((json \ "number").extract[Int])
        val name : String = (json \ "name").extract[String]
        val keywords : Seq[(String, Boolean)] = (json \ "keyword").extract[String].split(",").map(s => ("oeis_" + s) -> true)
        val comments : Seq[String] = (json \ "comment").extract[Seq[String]]
        val author : String = (json \ "author").extract[String]
        
        val (offset : Int, predata : Seq[BigInt]) = if (useBFile) {
            BFile(oeisID) : (Int, Seq[BigInt])
        } else {
            val predata : Seq[BigInt] = (json \ "data").extract[String].split(",").map(BigInt(_))
            val offset : Int = (json \ "offset").extract[String].split(",")(0).toInt
            (offset, predata)
        }
        
        val data : Seq[BigInt] = offset match {
            case k if k < 0 => predata.drop( - k)
            case 0 => predata
            case 1 => BigInt(1) +: predata
            case 2 => BigInt(1) +: BigInt(1) +: predata
            case n => throw new Exception("Weird OEIS offset at " + oeisID + ": " + n)
        }
        
        val bellTable : List[(Prime, List[Integer])] = (for (p <- primes.takeWhile(_ < data.length)) yield Prime(p) -> (
            for (e <- naturals.takeWhile(math.pow(p, _) < data.length)) yield Integer(data(math.pow(p, e).toInt))
        ).toList).toList
        
        return MultiplicativeFunction (
            mflabel = "MF-OEIS-" + oeisID,
            metadata = Metadata (
                descriptiveName = oeisID,
                verbalDefinition = name,
                comments = comments,
                authors = Seq(author),
                computationalOrigin = "Converted from OEIS using https://github.com/torstein-vik/zeta-types-scala-db",
                relatedObjects = Seq(URI("oeis://" + oeisID))
            ),
            properties = Record (keywords : _*),
            bellTable = BellTable (values = bellTable)
        )
    }
}
