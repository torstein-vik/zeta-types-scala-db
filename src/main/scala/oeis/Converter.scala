package io.github.torsteinvik.zetatypes.db.oeis

import io.github.torsteinvik.zetatypes.db._
import io.github.torsteinvik.zetatypes.db.dbmath.Primes._
import io.github.torsteinvik.zetatypes.db.Datatypes._

import org.json4s._

import scala.concurrent._
import ExecutionContext.Implicits.global

case class ConversionException(oeisID : String, msg : String) extends Exception("(" +oeisID+ "): " + msg)

object Converter{
    private implicit val formats = DefaultFormats
    
    private val multipicativityTests : Seq[(Int, (Int, Int))] = primes.take(5).combinations(2).toSeq.map{ case Seq(f1, f2) => (f1 * f2, (f1, f2)) }
    
    def apply(json : JObject, useBFile : Boolean = false) : Future[MultiplicativeFunction] = {
        val oeisID : String = "A%06d".format((json \ "number").extract[Int])
        val name : String = (json \ "name").extract[String]
        val keywords : Seq[(String, Boolean)] = (json \ "keyword").extract[String].split(",").map(s => ("oeis_" + s) -> true)
        val comments : Seq[String] = (json \ "comment").extract[Seq[String]]
        val author : String = (json \ "author").extract[String]
        
        // TODO: if f(1) != 1 exclude as it is not multipicative
        // TODO: Add test to check if it is multipicative for the first 100 values
        
        (if (useBFile) {
            BFile(oeisID) : Future[(Int, Seq[BigInt])]
        } else {
            val predata : Seq[BigInt] = (json \ "data").extract[String].split(",").map(BigInt(_))
            val offset : Int = (json \ "offset").extract[String].split(",")(0).toInt
            Future.successful((offset, predata))
        }).map { case (offset : Int, predata : Seq[BigInt]) => 
            val data : Seq[BigInt] = (offset match {
                case k if k < 0 => predata.drop( - k)
                case 0 => predata
                case 1 => BigInt(1) +: predata
                case 2 => BigInt(1) +: BigInt(1) +: predata
                case n => throw ConversionException(oeisID, s"Weird OEIS offset: $n")
            }).to[IndexedSeq]
            
            if(data(1) != 1) throw ConversionException(oeisID, s"Not multipicative: f(1) = ${data(1)} =/= 1")
            
            for ((v, (f1, f2)) <- multipicativityTests if v < data.length if data(v) != data(f1) * data(f2)) 
                throw ConversionException(oeisID, s"Not multipicative: f($v) = ${data(v)} =/= ${data(f1) * data(f2)} = f($f1) * f($f2)") 
            
            val bellTable : List[(Prime, List[Integer])] = (for (p <- primes.takeWhile(_ < data.length)) yield new Prime(p) -> (
                for (e <- Stream.from(0).takeWhile(math.pow(p, _) < data.length)) yield Integer(data(math.pow(p, e).toInt))
            ).toList).toList
            
            MultiplicativeFunction (
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
}
