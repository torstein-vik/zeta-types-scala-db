package io.github.torsteinvik.zetatypes.db.query

import io.github.torsteinvik.zetatypes.db._
import io.github.torsteinvik.zetatypes.db.codec._
import io.github.torsteinvik.zetatypes.db.datatypes._

abstract sealed class MFProperty[T] extends Property[T](PropertySubtypeLock) {def requires = Set(this)}
abstract sealed class JSONProperty[T] (val path : String*)(implicit val codec : Codec[T]) extends MFProperty[T]

object JSONProperty {
    def unapply[T] (p : JSONProperty[T]) : Option[Seq[String]] = p match {case p : JSONProperty[_] => Some(p.path) case _ => None}
    def unapplySeq[T] (p : JSONProperty[T]) : Option[Seq[String]] = unapply(p)
}

case object mf extends JSONProperty[MultiplicativeFunction]()
case object mflabel extends JSONProperty[String]("mflabel")
case object batchid extends JSONProperty[Option[String]]("metadata", "batchId")
case object name extends JSONProperty[String]("metadata", "descriptiveName")
case object definition extends JSONProperty[String]("metadata", "verbalDefinition")
case object comments extends JSONProperty[Seq[String]]("metadata", "comments")
case object properties extends JSONProperty[Record[Boolean]]("properties")
case object belltable extends JSONProperty[Seq[(Prime, Seq[ComplexNumber])]]("bellTable", "values")

case class bellcell(p : Prime, e : Nat) extends MFProperty[Option[ComplexNumber]]
case class bellrow(p : Prime) extends MFProperty[Seq[ComplexNumber]]
case class bellsmalltable(ps : Int = 10, es : Int = 15) extends MFProperty[Seq[(Prime, Seq[ComplexNumber])]]
