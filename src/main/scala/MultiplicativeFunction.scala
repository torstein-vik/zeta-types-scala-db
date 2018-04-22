package io.github.torsteinvik.zetatypes.db

import Datatypes._
import codec._
import org.json4s._

case class MultiplicativeFunction (
    mflabel: String, 
    metadata: Metadata, 
    properties: Record[Boolean], 
    invariants: Record[ComplexNumber], 
    bellTable: BellTable, 
    globalTannakianSymbol: Option[GlobalTannakianSymbol] = None, 
    functionalEquationParameters: Option[FunctionalEquationParameters] = None, 
    etaCombination: Option[EtaCombination] = None
)

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

case class GlobalTannakianSymbol (
    exceptionalPrimes: Seq[Prime], 
    localValues: Seq[(Prime, HybridSet[ComplexNumber])], 
    primeLogForm: Option[PrimeLogSymbol] = None, 
    polynomialForm: Option[HybridSet[ComplexPolynomial]] = None, 
    modulusForm: Option[ModulusForm] = None
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



object MultiplicativeFunction extends CodecContainer[MultiplicativeFunction](
    {
        case MultiplicativeFunction(mflabel, metadata, properties, invariants, bellTable, globalTannakianSymbol, functionalEquationParameters, etaCombination) => JObject(List(
            JField("mflabel", encode(mflabel)),
            JField("metadata", encode(metadata)),
            JField("properties", encode(properties)),
            JField("invariants", encode(invariants)),
            JField("bellTable", encode(bellTable)),
            JField("globalTannakianSymbol", encode(globalTannakianSymbol)),
            JField("functionalEquationParameters", encode(functionalEquationParameters)),
            JField("etaCombination", encode(etaCombination))
        ))
    },
    {
        case json => new MultiplicativeFunction(
            mflabel = decode[String](json \ "mflabel"),
            metadata = decode[Metadata](json \ "metadata"),
            properties = decode[Record[Boolean]](json \ "properties"),
            invariants = decode[Record[ComplexNumber]](json \ "invariants"),
            bellTable = decode[BellTable](json \ "bellTable"),
            globalTannakianSymbol = decode[Option[GlobalTannakianSymbol]](json \ "globalTannakianSymbol"),
            functionalEquationParameters = decode[Option[FunctionalEquationParameters]](json \ "functionalEquationParameters"),
            etaCombination = decode[Option[EtaCombination]](json \ "etaCombination")
        )
    }
)

object Metadata extends CodecContainer[Metadata](
    {
        case Metadata(descriptiveName, verbalDefinition, formalDefinition, latexMacroUnapplied, latexMacroApplied, comments, firstAddedTimestamp, lastChangedTimestamp, authors, computationalOrigin, batchId, relatedObjects) => JObject(List(
            JField("descriptiveName", encode(descriptiveName)),
            JField("verbalDefinition", encode(verbalDefinition)),
            JField("formalDefinition", encode(formalDefinition)),
            JField("latexMacroUnapplied", encode(latexMacroUnapplied)),
            JField("latexMacroApplied", encode(latexMacroApplied)),
            JField("comments", encode(comments)),
            JField("firstAddedTimestamp", encode(firstAddedTimestamp)),
            JField("lastChangedTimestamp", encode(lastChangedTimestamp)),
            JField("authors", encode(authors)),
            JField("computationalOrigin", encode(computationalOrigin)),
            JField("batchId", encode(batchId)),
            JField("relatedObjects", encode(relatedObjects))
        ))
    },
    {
        case json => new Metadata(
            descriptiveName = decode[String](json \ "descriptiveName"), 
            verbalDefinition = decode[String](json \ "verbalDefinition"), 
            formalDefinition = decode[Option[String]](json \ "formalDefinition"), 
            latexMacroUnapplied = decode[Option[String]](json \ "latexMacroUnapplied"), 
            latexMacroApplied = decode[Option[String]](json \ "latexMacroApplied"), 
            comments = decode[Seq[String]](json \ "comments"), 
            firstAddedTimestamp = decode[String](json \ "firstAddedTimestamp"), 
            lastChangedTimestamp = decode[String](json \ "lastChangedTimestamp"), 
            authors = decode[Seq[String]](json \ "authors"), 
            computationalOrigin = decode[String](json \ "computationalOrigin"), 
            batchId = decode[String](json \ "batchId"), 
            relatedObjects = decode[Seq[URI]](json \ "relatedObjects") 
        )
    }
)

object URI extends CodecContainer[URI](
    {
        case URI(uri) => JObject(List(
            JField("uri", encode(uri))
        ))
    },
    {
        case json => new URI(
            uri = decode[String](json \ "uri")
        )
    }
)

object BellTable extends CodecContainer[BellTable](
    {
        case BellTable(masterEquation, formalMasterEquation, values, eulerFactors) => JObject(List(
            JField("masterEquation", encode(masterEquation)),
            JField("formalMasterEquation", encode(formalMasterEquation)),
            JField("values", encode(values)),
            JField("eulerFactors", encode(eulerFactors))
        ))
    },
    {
        case json => new BellTable(
            masterEquation = decode[Option[String]](json \ "masterEquation"), 
            formalMasterEquation = decode[Option[String]](json \ "formalMasterEquation"), 
            values = decode[Seq[(Prime, Seq[ComplexNumber])]](json \ "values"), 
            eulerFactors = decode[Seq[(Prime, ComplexPolynomial, ComplexPolynomial)]](json \ "eulerFactors") 
        )
    }
)

object GlobalTannakianSymbol extends CodecContainer[GlobalTannakianSymbol](
    {
        case GlobalTannakianSymbol(exceptionalPrimes, localValues, primeLogForm, polynomialForm, modulusForm) => JObject(List(
            JField("exceptionalPrimes", encode(exceptionalPrimes)),
            JField("localValues", encode(localValues)),
            JField("primeLogForm", encode(primeLogForm)),
            JField("polynomialForm", encode(polynomialForm)),
            JField("modulusForm", encode(modulusForm))
        ))
    },
    {
        case json => new GlobalTannakianSymbol(
            exceptionalPrimes = decode[Seq[Prime]](json \ "exceptionalPrimes"), 
            localValues = decode[Seq[(Prime, HybridSet[ComplexNumber])]](json \ "localValues"), 
            primeLogForm = decode[Option[PrimeLogSymbol]](json \ "primeLogForm"), 
            polynomialForm = decode[Option[HybridSet[ComplexPolynomial]]](json \ "polynomialForm"), 
            modulusForm = decode[Option[ModulusForm]](json \ "modulusForm") 
        )
    }
)

