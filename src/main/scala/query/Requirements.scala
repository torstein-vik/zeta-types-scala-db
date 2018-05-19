package io.github.torsteinvik.zetatypes.db.query

final class Requirements(requirements : Set[MFProperty[_]]) {
    
    type PropertyValue[T] = (MFProperty[T], T)
    
    val minimal : Set[MFProperty[_]] = requirements -- (for {
        superreq <- requirements
        subreq <- requirements
        if superreq != subreq
        if MFPropertyProvider.provides(superreq, subreq)
    } yield subreq)
    
    if (requirements.exists(req => !minimal.exists(MFPropertyProvider.provides(_, req)))) throw new Exception(f"Requirement minimization failed! $minimal cannot provide $requirements")
    
    // TODO: Account for case where minimal = Set()
    def assembleProvider(pointer : QueryPointer) : MFPropertyProvider = {
        def evalPropertyValue[T] (property : MFProperty[T]) : PropertyValue[property.output] = (property, pointer.evalMFProperty(property))
        
        val values : Seq[PropertyValue[_]] = minimal.toSeq.map(prop => evalPropertyValue[prop.output](prop.asInstanceOf[MFProperty[prop.output]]))
        
        values.map{case (property, value) => MFPropertyProvider(property)(value)}.reduce[MFPropertyProvider](_ ++ _)
        
    }
}
