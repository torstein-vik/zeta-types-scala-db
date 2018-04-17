package io.github.torsteinvik.zetatypes.db

import Datatypes._


case class Metadata (
    descriptiveName: String, 
    verbalDefinition: String, 
    formalDefinition: Option[String] = None, 
    latexMacroUnapplied: Option[String] = None, 
    latexMacroApplied: Option[String] = None, 
    comments: Seq[String], 
    firstAddedTimestamp: String, 
    lastChangedTimestamp: String, 
    authors: Seq[String], 
    computationalOrigin: String, 
    batchId: String, 
    relatedObjects: Seq[URI] 
)

case class URI (
    uri : String 
)

