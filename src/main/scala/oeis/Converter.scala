package io.github.torsteinvik.zetatypes.db.oeis

import io.github.torsteinvik.zetatypes.db._
import io.github.torsteinvik.zetatypes.db.Datatypes._

import org.json4s._

object Converter{
    private implicit val formats = DefaultFormats
    
    //credit: https://stackoverflow.com/questions/8566532/scala-streams-and-their-memory-usage
    private lazy val naturals: Stream[Int] = Stream.cons(0, naturals.map{_ + 1})
    
    //credit: https://gist.github.com/ramn/8378315
    private lazy val primes: Stream[Int] = 2 #:: Stream.from(3).filter { n => !primes.takeWhile(_ <= math.sqrt(n)).exists(n % _ == 0) }
    
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