package io.github.torsteinvik.zetatypes.test.db

import org.scalatest.FunSuite

import io.github.torsteinvik.zetatypes.db._
import io.github.torsteinvik.zetatypes.db.Datatypes._
import io.github.torsteinvik.zetatypes.db.codec._

class CodecTest extends FunSuite {
    
    
    test("Define codec for impossible types") {
        assertThrows[Exception] { Codec[Option[_]] }
    }
    
    test("Define codec for possible types") {
        Codec[BigInt]
        Codec[Double]
        Codec[String]
    }
    
    test("Define codec for MultiplicativeFunction") { Codec[MultiplicativeFunction] }
    
    test("Encode MultiplicativeFunction"){
        val codec = Codec[MultiplicativeFunction]
        
        val mf = MultiplicativeFunction (
            mflabel = "MF-2000",
            metadata = Metadata (
                descriptiveName = "The 2000 multiplicative function",
                verbalDefinition = "It's just so 2000",
                latexMacroApplied = Some("2000"),
                latexMacroUnapplied = Some("2000(#1)"),
                comments = Seq (
                    "This entry isn't properly documented",
                    "What idiot added this"
                ),
                firstAddedTimestamp = "2000",
                lastChangedTimestamp = "2002",
                authors = Seq("Virstein Tok"),
                computationalOrigin = "2000",
                batchId = "2000",
                relatedObjects = Seq(URI("mflabel://mf-1999"), URI("mflabel://mf-2001"))
            ),
            properties = Record (
                "abelian"       -> true,
                "heteromorphic" -> false
            ),
            invariants = Record (
                "unity" -> PolarComplex(12, 0.5),
                "root"  -> CartesianComplex(Ratio(1,3), 2)
            ),
            bellTable = BellTable (
                values = Seq(
                    Prime(2) -> Seq(1, 2, 4, 8, 16),
                    Prime(3) -> Seq(1, 2, 4, 8, 16),
                    Prime(5) -> Seq(1, 2, 4, 8, 16),
                    Prime(7) -> Seq(1, 2, 4, 8, 16)
                ),
                eulerFactors = Seq()
            ),
            globalTannakianSymbol = Some(GlobalTannakianSymbol(
                exceptionalPrimes = Seq(11, 13),
                localValues = Seq(),
                polynomialForm = Some(HybridSet(
                    ComplexPolynomial(Nat(12) -> Nat(2)) -> 12
                ))
            )),
            functionalEquationParameters = Some(FunctionalEquationParameters(
                degree = Some(2),
                conductor = Some(17),
                signature = Some((2, 2)),
                spectralParameterListR = Some(Seq(1)),
                spectralParameterListC = Some(Seq(1.5)),
                sign = Some(-1)
            )),
            etaCombination = Some(EtaCombination(
                isProven = true,
                elements = HybridSet(
                    HybridSet(Nat(2) -> 12) -> 1
                )
            ))
        )
        
        assert(mf === codec.decode(codec.encode(mf)))
        
    }
    
}
