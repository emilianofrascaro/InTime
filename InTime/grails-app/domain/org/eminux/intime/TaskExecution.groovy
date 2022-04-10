package org.eminux.intime

class TaskExecution {

    String server
    String stato
    Task task
    Date dataEsecuzione = new Date()
    Schedulazione schedulazione

    static constraints = {
        server nullable:false, blank:false
        stato nullable:false, blank:false
        task nullable:false
        dataEsecuzione nullable:false
        schedulazione nullable:true
    }

    static mapping = {
        sort dataEsecuzione: "desc" // or "asc"
    }
}
