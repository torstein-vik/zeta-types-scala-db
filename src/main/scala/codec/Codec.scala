package io.github.torsteinvik.zetatypes.db.codec


import org.json4s._

trait Codec[T] {
    def encode (x : T) : JValue
    def decode (x : JValue) : T
}

