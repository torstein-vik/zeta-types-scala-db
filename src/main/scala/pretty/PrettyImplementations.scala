package io.github.torsteinvik.zetatypes.db.pretty

private[pretty] trait PrettyImplementations {
    implicit def prettyInt[M <: PrettyMode] = new PrettyNoOptions[Int, M]{
        def apply(n : Int) = n.toString
    }
}
