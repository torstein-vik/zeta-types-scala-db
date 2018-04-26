package io.github.torsteinvik.zetatypes.db.oeis



object Download {
    
    def queryurl(i : Int) = "https://oeis.org/search?q=keyword:mult&fmt=json&start=" + (i * 10)
    
}
