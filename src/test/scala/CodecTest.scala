package io.github.torsteinvik.zetatypes.test.db

import org.scalatest.FunSuite

import io.github.torsteinvik.zetatypes.db._
import io.github.torsteinvik.zetatypes.db.Datatypes._
import io.github.torsteinvik.zetatypes.db.codec._

class CodecTest extends FunSuite {
    
    test("Codec for basic types") {
        
        assert(decode[Boolean](encode(true)) === true)
        assert(decode[Boolean](encode(false)) === false)
        
        assert(decode[BigInt](encode(BigInt("13"))) === BigInt("13"))
        assert(decode[BigInt](encode(BigInt("-135532525253"))) === BigInt("-135532525253"))
        assert(decode[BigInt](encode(BigInt("43252356"))) === BigInt("43252356"))
        assert(decode[BigInt](encode(BigInt("13"))) !== BigInt("12"))
        
        assert(decode[BigInt](encode(BigInt("4523523653487768978586534252345135647847564255452462354737356734234562514567356748"))) === BigInt("4523523653487768978586534252345135647847564255452462354737356734234562514567356748"))
        assert(decode[BigInt](encode(BigInt("438901271589364278213891256374657812389576890257605948153294128571487513795289768912436572784673465637482456234735684789457426345234762768457364524512376245736179"))) === BigInt("438901271589364278213891256374657812389576890257605948153294128571487513795289768912436572784673465637482456234735684789457426345234762768457364524512376245736179"))
        assert(decode[BigInt](encode(BigInt("-4523523653487768978586534252345135647847564255452462354737356734234562514567356748"))) === BigInt("-4523523653487768978586534252345135647847564255452462354737356734234562514567356748"))
        assert(decode[BigInt](encode(BigInt("-438901271589364278213891256374657812389576890257605948153294128571487513795289768912436572784673465637482456234735684789457426345234762768457364524512376245736179"))) === BigInt("-438901271589364278213891256374657812389576890257605948153294128571487513795289768912436572784673465637482456234735684789457426345234762768457364524512376245736179"))
        
        assert(decode[BigInt](encode(BigInt(2) pow 32)) === (BigInt(2) pow 32))
        assert(decode[BigInt](encode(BigInt(2) pow 64)) === (BigInt(2) pow 64))
        assert(decode[BigInt](encode((BigInt(2) pow 32) - 1)) === (BigInt(2) pow 32) - 1)
        assert(decode[BigInt](encode((BigInt(2) pow 32) + 1)) === (BigInt(2) pow 32) + 1)
        
        assert(decode[Double](encode(121.243)) === 121.243)
        assert(decode[Double](encode(12E23)) === 12E23)
        assert(decode[Double](encode(-5E-21)) === -5E-21)
        assert(decode[Double](encode(-4E-21)) !== -3E-21)
        
        assert(decode[String](encode("test1..%&\"")) === "test1..%&\"")
        assert(decode[String](encode("testy test +++--- <3")) === "testy test +++--- <3")
        assert(decode[String](encode("''''hey$:::")) === "''''hey$:::")
        assert(decode[String](encode("halo")) !== "halo ")
        
        
        assert(decode[Option[BigInt]](encode[Option[BigInt]](Some(BigInt(123)))) === Some(BigInt(123)))
        assert(decode[Option[Double]](encode[Option[Double]](Some(-5E-21))) === Some(-5E-21))
        assert(decode[Option[String]](encode[Option[String]](Some("hello"))) === Some("hello"))
        assert(decode[Option[BigInt]](encode[Option[BigInt]](Some(BigInt(-2)))) !== Some(BigInt(12)))
        assert(decode[Option[Double]](encode[Option[Double]](Some(-2.12))) !== Some(12.12))
        assert(decode[Option[String]](encode[Option[String]](Some("test"))) !== Some("test "))
        assert(decode[Option[BigInt]](encode[Option[BigInt]](Some(BigInt(0)))) !== None)
        assert(decode[Option[Double]](encode[Option[Double]](Some(Double.NaN))) !== None)
        assert(decode[Option[String]](encode[Option[String]](Some(""))) !== None)
        assert(decode[Option[BigInt]](encode[Option[BigInt]](None)) === None)
        assert(decode[Option[Double]](encode[Option[Double]](None)) === None)
        assert(decode[Option[String]](encode[Option[String]](None)) === None)
        
        assert(decode[List[BigInt]](encode(List(BigInt(1), BigInt(2), BigInt(3)))) === List(BigInt(1), BigInt(2), BigInt(3)))
        assert(decode[List[Double]](encode(List(1.2, 2.4, 3.5))) === List(1.2, 2.4, 3.5))
        assert(decode[List[String]](encode(List("hey", "there", "!"))) === List("hey", "there", "!"))
        
        assert(decode[Seq[BigInt]](encode(Seq(BigInt(1), BigInt(2), BigInt(3)))) === Seq(BigInt(1), BigInt(2), BigInt(3)))
        assert(decode[Seq[Double]](encode(Seq(1.2, 2.4, 3.5))) === Seq(1.2, 2.4, 3.5))
        assert(decode[Seq[String]](encode(Seq("hey", "there", "!"))) === Seq("hey", "there", "!"))
        
        assert(decode[(String, Double)](encode(("hey", 1.2))) ===(("hey", 1.2)))
        assert(decode[(String, (Double, String))](encode(("hey", (1.2, "hallo")))) ===(("hey", (1.2, "hallo"))))
        assert(decode[(String, (BigInt, BigInt))](encode(("hey", (BigInt(4), BigInt(3))))) ===(("hey", (BigInt(4), BigInt(3)))))
        
        assert(decode[(String, BigInt, BigInt)](encode(("hey", BigInt(4), BigInt(3)))) ===(("hey", BigInt(4), BigInt(3))))
    }
    
    test("Codec for ComplexNumber") {
        val xs : Seq[ComplexNumber] = Seq(
            Nat(12), 
            Prime(7), 
            Nat(3), 
            Integer(-2), 
            Integer(0), 
            Floating(12E-23), 
            Floating(1.4322E22), 
            Ratio(1, 2), 
            Ratio(1, 3), 
            CartesianComplex(Ratio(1, 3), Floating(12E-23)), 
            CartesianComplex(Integer(2), Floating(2)),
            CartesianComplex(Integer(-4), Ratio(2, 4)),
            PolarComplex(Integer(7), Ratio(1, 2)),
            PolarComplex(Integer(7), Floating(1/2 + 5E-7))
        )  
        
        for (x <- xs) {
            assert(decode[ComplexNumber](encode[ComplexNumber](x)) === x)
        }    
    }
    
    test("Codec for ComplexPolynomial") {
        val ys : Seq[ComplexPolynomial] = Seq(
            ComplexPolynomial(Integer(-2) -> 12, PolarComplex(Integer(7), Ratio(1, 2)) -> 1, Floating(1.4324) -> 0),
            ComplexPolynomial(Ratio(2, 3) -> 2, Floating(2.32E23) -> Nat(BigInt("485734807857345873485720239234857280")))
        )
        
        for (y <- ys) {
            assert(decode[ComplexPolynomial](encode[ComplexPolynomial](y)) === y)
        }
    }
    
    test("Codec for hybrid sets") {
        val hss = HybridSet[String]("hey" -> 12, "hola" -> 14)
        assert(decode[HybridSet[String]](encode[HybridSet[String]](hss)) === hss)
        
        val hsc = HybridSet[ComplexNumber](
            PolarComplex(Integer(7), Floating(1/2 + 5E-7)) -> -34, 
            CartesianComplex(Integer(2), Floating(2)) -> Integer(BigInt("-489734589034718524589234563478965712465746716514789561234796"))
        )
        assert(decode[HybridSet[ComplexNumber]](encode[HybridSet[ComplexNumber]](hsc)) === hsc)
    }
    
    test("Codec for records") {
        val rec1 = Record[String](
            "hey" -> "hello",
            "final" -> "Not quite",
            "static" -> "plausible"
        )
        assert(decode[Record[String]](encode[Record[String]](rec1)) === rec1)
        
        val rec2 = Record[ComplexNumber](
            "sign eval" -> PolarComplex(Integer(12), Ratio(1, 2)),
            "conductor" -> Nat(13),
            "first bad prime" -> Prime(13)
        )
        assert(decode[Record[ComplexNumber]](encode[Record[ComplexNumber]](rec2)) === rec2)
    }
    
    test("Codec for MultiplicativeFunction"){        
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
                authors = Seq("Virstein Tok"),
                computationalOrigin = "2000",
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
        
        assert(decode[MultiplicativeFunction](encode(mf)) === mf)
        
    }
    
}
