package org.eminux.intime

class Task {

    public static Integer STATO_ATTIVO = 0
    public static Integer STATO_ARCHIVIATO = 1

    String command
    String descrizione
    Integer stato = 0

    static constraints = {
        command blank:false,nullable:false
        descrizione blank:false,nullable:false
        stato nullable:false
    }
}
