package io.github.torsteinvik.zetatypes.db.query

final class MFPropertyProvider(val provider : PartialFunction[MFProperty[_], _]) {
    final def provide[T] (providee : MFProperty[T]) : T = if(provider.isDefinedAt(providee)) provider(providee).asInstanceOf[T] else throw new Exception("Property requested but not in requirements! " + providee)
    
    final def ++ (other : MFPropertyProvider) = new MFPropertyProvider(provider orElse other.provider)
}

