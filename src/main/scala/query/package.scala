package io.github.torsteinvik.zetatypes.db

/** This pacakge provides dsl for creating queries, as well as a direct querying system
 *  @author Torstein Vik
 */
package object query {
    implicit class Helper_~[T](t : T){
        def ~[S](s : S) = new ~(t, s)
    }
} 
