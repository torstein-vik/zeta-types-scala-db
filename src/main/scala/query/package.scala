package io.github.torsteinvik.zetatypes.db

/** This pacakge provides dsl for creating queries, as well as a naive query system
 *  @author Torstein Vik
 */
package object query {
    type ~[S, T] = (S, T)
    
    import scala.language.implicitConversions
    implicit def propertyAsQuery[T](p : Property[T]) : PropertyQuery[T] = new SinglePropertyQuery[T](p)
} 
