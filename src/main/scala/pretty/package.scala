package io.github.torsteinvik.zetatypes.db

/** This pacakge provides prettification of properties and datatypes, both for html and latex
 *  @author Torstein Vik
 */
package object pretty {
    type Plain = Plain.type
    type HTML = HTML.type
    type LaTeX = LaTeX.type
    
    type NoOptions = NoOptions.type
    
    def pretty[T, O <: Options](t : T)(implicit pretty : Pretty[T, Plain, O], f : NoOptions => O) : String = pretty(t)(f(NoOptions))
    def html[T, O <: Options]  (t : T)(implicit pretty : Pretty[T, HTML, O],  f : NoOptions => O) : String = pretty(t)(f(NoOptions))
    def latex[T, O <: Options] (t : T)(implicit pretty : Pretty[T, LaTeX, O], f : NoOptions => O) : String = pretty(t)(f(NoOptions))
    
    def pretty[T, O <: Options](t : T, options : O)(implicit pretty : Pretty[T, Plain, O]) : String = pretty(t)(options)
    def html[T, O <: Options]  (t : T, options : O)(implicit pretty : Pretty[T, HTML, O]) : String = pretty(t)(options)
    def latex[T, O <: Options] (t : T, options : O)(implicit pretty : Pretty[T, LaTeX, O]) : String = pretty(t)(options)
}
