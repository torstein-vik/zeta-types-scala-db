package io.github.torsteinvik.zetatypes.db.pretty

import scala.annotation._

@implicitNotFound(msg = "Could not find a way to prettify ${T} into ${M}-format with options of type ${O}")
trait Pretty[-T, M <: PrettyMode, O <: Options] {
    def apply (t : T)(o : O) : String
}

trait PrettyNoOptions[-T, M <: PrettyMode] extends Pretty[T, M, NoOptions] {
    def apply (t : T) : String
    final override def apply (t : T)(o : NoOptions) : String = apply(t)
}

object Pretty extends PrettyImplementations
