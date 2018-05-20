package io.github.torsteinvik.zetatypes.db.datatypes

import io.github.torsteinvik.zetatypes.db.codec._
import org.json4s._

/** A JSON object, each value of specified type */
case class Record[T] (entries : (String, T)*)

object Record {
    implicit def recordCodec[A](implicit codec : Codec[A]) : Codec[Record[A]] = new Codec[Record[A]] {
        def encode (x : Record[A]) = JObject(x.entries.map{case (field, a) => JField(field, codec.encode(a))}.toList)
        def decode (x : JValue) = x match {
            case JObject(ls) => Record[A](ls.map{case JField(field, a) => (field, codec.decode(a))} : _*)
            case _ => throw new CodecException("Found " + x + " expected object (JObject)")
        }
    }
}
