package org.eminux.intime

class TaskLog {

    TaskExecution execution
    String tipoLog = "output" //se Output o Error o End
    String logMessage
    Date oraLog = new Date()

    static constraints = {
        execution nullable:false
        tipoLog nullable:false,blank:false
        logMessage nullable:false,blank:false
        oraLog nullable:false
    }
}
