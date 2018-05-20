package io.github.torsteinvik.zetatypes.db.datatypes

import io.github.torsteinvik.zetatypes.db._
import io.github.torsteinvik.zetatypes.db.codec._
import org.json4s._

/** A Hybrid set of elements in the input type */
case class HybridSet[A] (multiplicities : (A, Integer)*)

object HybridSet {
    implicit def hybridSetCodec[A](implicit codecA : Codec[A]) : Codec[HybridSet[A]] = new Codec[HybridSet[A]] {
        def encode (x : HybridSet[A]) = codec.encode[List[(A, Integer)]](x.multiplicities.toList)
        def decode (x : JValue) = HybridSet[A](codec.decode[List[(A, Integer)]](x) : _*)
    }
}
