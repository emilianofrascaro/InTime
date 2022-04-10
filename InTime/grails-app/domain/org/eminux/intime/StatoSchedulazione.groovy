package org.eminux.intime

class StatoSchedulazione {

    public static Integer SCHEDULAZIONE_ATTIVA = 0
    public static Integer SCHEDULAZIONE_NON_ATTIVA = 1
    public static Integer SCHEDULAZIONE_ELIMINATA = 2

    Integer codice
    String descrizione

    static constraints = {
        codice nullable:false
        descrizione nullable:false, blank:false
    }
}
