package io.github.torsteinvik.zetatypes.test.db

import org.scalatest.FunSuite

import io.github.torsteinvik.zetatypes.db._
import io.github.torsteinvik.zetatypes.db.Datatypes._
import io.github.torsteinvik.zetatypes.db.query._
import io.github.torsteinvik.zetatypes.db.query.Property._

class DirectQueryTest extends FunSuite {
    val mf1 = MultiplicativeFunction(
        mflabel = "MF-OEIS-A000005",
        metadata = Metadata(
            descriptiveName = "A000005",
            verbalDefinition = "d(n) (also called tau(n) or sigma_0(n)), the number of divisors of n.",
            comments = List(
                "If the canonical factorization of n into prime powers is Product p^e(p) then d(n) = Product (e(p) + 1). More generally, for k>0, sigma_k(n) = Product_p ((p^((e(p)+1)*k))-1)/(p^k-1).", 
                "Number of ways to write n as n = x*y, 1 <= x <= n, 1 <= y <= n. For number of unordered solutions to x*y=n, see A038548.",
                "Note that d(n) is not the number of Pythagorean triangles with radius of the inscribed circle equal to n (that is A078644). For number of primitive Pythagorean triangles having inradius n, see A068068(n).",
                "Number of factors in the factorization of the polynomial x^n-1 over the integers. - _T. D. Noe_, Apr 16 2003",
                "d(n) is odd if and only if n is a perfect square. If d(n) = 2, n is prime. - Donald Sampson (Marsquo(AT)hotmail.com), Dec 10 2003",
                "Also equal to the number of partitions p of n such that all the parts have the same cardinality, i.e. max(p)=min(p). - _Giovanni Resta_, Feb 06 2006", 
                "Equals A127093 as an infinite lower triangular matrix * the harmonic series, [1/1, 1/2, 1/3, ...]. - _Gary W. Adamson_, May 10 2007", 
                "Sum_{n>0} d(n)/(n^n) = Sum_{n>0, m>0} 1/(n*m). - _Gerald McGarvey_, Dec 15 2007", 
                "For odd n, this is the number of partitions of n into consecutive integers. Proof: For n = 1, clearly true. For n = 2k + 1, k >= 1, map each (necessarily odd) divisor to such a partition as follows: For 1 and n, map k + (k+1) and n, respectively. For any remaining divisor d <= sqrt(n), map (n/d - (d-1)/2) + ... + (n/d - 1) + (n/d) + (n/d + 1) + ... + (n/d + (d-1)/2) {i.e., n/d plus (d-1)/2 pairs each summing to 2n/d}. For any remaining divisor d > sqrt(n), map ((d-1)/2 - (n/d - 1)) + ... + ((d-1)/2 - 1) + (d-1)/2 + (d+1)/2 + ((d+1)/2 + 1) + ... + ((d+1)/2 + (n/d - 1)) {i.e., n/d pairs each summing to d}. As all such partitions must be of one of the above forms, the 1-to-1 correspondence and proof is complete. - _Rick L. Shepherd_, Apr 20 2008",
                "Number of subgroups of the cyclic group of order n. - _Benoit Jubin_, Apr 29 2008", 
                "Equals row sums of triangle A143319. - _Gary W. Adamson_, Aug 07 2008", 
                "Equals row sums of triangle A159934, equivalent to generating a(n) by convolving A000005 prefaced with a 1; (1, 1, 2, 2, 3, 2, ...) with the INVERTi transform of A000005, (A159933): (1, 1,-1, 0, -1, 2, ...). Example: a(6) = 4 = (1, 1, 2, 2, 3, 2) dot (2, -1, 0, -1, 1, 1) = (2, -1, 0, -2, 3, 2) = 4. - _Gary W. Adamson_, Apr 26 2009", 
                "Number of times n appears in an n X n multiplication table. - _Dominick Cancilla_, Aug 02 2010", 
                "Number of k >= 0 such that (k^2 + k*n + k)/(k + 1) is an integer. - _Juri-Stepan Gerasimov_, Oct 25 2015.", 
                "The only numbers n such that tau(n) >= n/2 are 1,2,3,4,6,8,12. - _Michael De Vlieger_, Dec 14 2016", 
                "a(n) is also the number of partitions of 2*n into equal parts, minus the number of partitions of 2*n into consecutive parts. - _Omar E. Pol_, May 03 2017"
            ),
            computationalOrigin = "Converted from OEIS using https://github.com/torstein-vik/zeta-types-scala-db, modified for testing",
            batchId = Some("#5463f897 - 1502")
        ),
        properties = Record(("oeis_easy",true), ("oeis_core",true), ("oeis_nonn",true), ("oeis_nice",true), ("oeis_mult",true), ("oeis_hear",true), ("oeis_changed",true)),
        bellTable = BellTable(
            values = List(
                (Prime(2),List(Integer(1), Integer(2), Integer(3), Integer(4), Integer(5), Integer(6), Integer(7))),
                (Prime(3),List(Integer(1), Integer(2), Integer(3), Integer(4), Integer(5))), 
                (Prime(5),List(Integer(1), Integer(2), Integer(3))), 
                (Prime(7),List(Integer(1), Integer(2), Integer(3))), 
                (Prime(11),List(Integer(1), Integer(2))), 
                (Prime(13),List(Integer(1), Integer(2))), 
                (Prime(17),List(Integer(1), Integer(2))), 
                (Prime(19),List(Integer(1), Integer(2))), 
                (Prime(23),List(Integer(1), Integer(2))), 
                (Prime(29),List(Integer(1), Integer(2))), 
                (Prime(31),List(Integer(1), Integer(2))), 
                (Prime(37),List(Integer(1), Integer(2))), 
                (Prime(41),List(Integer(1), Integer(2))), 
                (Prime(43),List(Integer(1), Integer(2))), 
                (Prime(47),List(Integer(1), Integer(2))), 
                (Prime(53),List(Integer(1), Integer(2))), 
                (Prime(59),List(Integer(1), Integer(2))), 
                (Prime(61),List(Integer(1), Integer(2))), 
                (Prime(67),List(Integer(1), Integer(2))), 
                (Prime(71),List(Integer(1), Integer(2))), 
                (Prime(73),List(Integer(1), Integer(2))), 
                (Prime(79),List(Integer(1), Integer(2))), 
                (Prime(83),List(Integer(1), Integer(2))), 
                (Prime(89),List(Integer(1), Integer(2))), 
                (Prime(97),List(Integer(1), Integer(2))), 
                (Prime(101),List(Integer(1), Integer(2))), 
                (Prime(103),List(Integer(1), Integer(2)))
            )
        )
    )
    val mf2 = MultiplicativeFunction(
        mflabel = "MF-Test-1",
        metadata = Metadata(
            descriptiveName = "Test function - dummy",
            verbalDefinition = "A dummy test function for testing the direct query system. nr 1. Contains the word 'eta'",
            comments = List(
                "Comment 1 - ",
                "Comment 2 - "
            ),
            computationalOrigin = "Defined explicitly in DirectQueryTest.scala"
        ),
        properties = Record(),
        bellTable = BellTable(
            values = List(
                (Prime(2),List(Integer(1), Integer(7), Integer(1), Integer(1)))
            )
        )
    )
    
    val mfs = Seq(mf1, mf2)
    def query[T](q : Query[T]) : Seq[T] = DirectQuery.query(q)(mfs)
    
    test("basic queries direct") {
        assert( query(mf) === Seq(mf1, mf2))
        assert( query(mf ~ mflabel ~ mfvalue(2).get) === Seq(mf1 ~ "MF-OEIS-A000005" ~ CartesianComplex(Floating(2), Floating(0)), mf2 ~ "MF-Test-1" ~ CartesianComplex(Floating(7), Floating(0))))
    }
    
}
