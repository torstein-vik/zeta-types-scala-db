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

case class BellTable (
    masterEquation: Option[String] = None, 
    formalMasterEquation: Option[String] = None, 
    values: Seq[(Prime, Seq[ComplexNumber])], 
    eulerFactors: Seq[(Prime, ComplexPolynomial, ComplexPolynomial)] 
)


case class ModulusForm (
    modulus: Nat,
    symbols: Seq[PrimeLogSymbol]
)

case class FunctionalEquationParameters (
    degree: Option[Nat] = None, 
    conductor: Option[Nat] = None, 
    signature: Option[(Nat, Nat)] = None, 
    spectralParameterListR: Option[Seq[ComplexNumber]] = None, 
    spectralParameterListC: Option[Seq[ComplexNumber]] = None, 
    sign: Option[ComplexNumber] = None
)

case class EtaCombination (
    elements: HybridSet[HybridSet[Nat]],
    isProven: Boolean
)
