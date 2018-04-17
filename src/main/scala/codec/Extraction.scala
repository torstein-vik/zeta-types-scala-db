package io.github.torsteinvik.zetatypes.db.codec

import scala.reflect.runtime.universe._

import org.json4s._

private[codec] object Extraction {
    def apply[T](implicit tt : TypeTag[T]) : Codec[_] = {
    }
    
}

/*

Needs to handle:

* case classes
* Option
* String
* BigInt
* Double
* Record
* List (*)

*/
