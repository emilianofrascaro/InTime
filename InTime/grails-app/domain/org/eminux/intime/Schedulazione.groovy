package org.eminux.intime

class Schedulazione {

    Task taskToExecute
    String cronExpression
    StatoSchedulazione status
    String serverName

    static constraints = {
        cronExpression blank:false, nullable:false
        serverName blank:false, nullable:false
        status nullable:false
    }
}
