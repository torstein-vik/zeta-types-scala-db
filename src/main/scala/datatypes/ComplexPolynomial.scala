package io.github.torsteinvik.zetatypes.db.datatypes

import io.github.torsteinvik.zetatypes.db.codec._
import org.json4s._

/** A polynomial with coefficients in the [[ComplexNumber]]s, stored sparsely */
case class ComplexPolynomial (coeffs : (ComplexNumber, Nat)*) 

object ComplexPolynomial extends CodecContainer[ComplexPolynomial](
    { case ComplexPolynomial(x @ _*) => JObject(List(JField("monomials", encode[List[(ComplexNumber, Nat)]](x.toList))))},
    { case JObject(List(JField("monomials", x))) => new ComplexPolynomial(decode[List[(ComplexNumber, Nat)]](x) : _*)}
)
