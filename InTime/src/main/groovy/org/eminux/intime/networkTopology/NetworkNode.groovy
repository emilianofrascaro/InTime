package org.eminux.intime.networkTopology

class NetworkNode {

    public static String STATUS_ONLINE = 'ON_LINE'
    public static String STATUS_OFFLINE = 'OFF_LINE'
    public static String STATUS_READY = 'READY'

    def nome
    def ipAddress
    def status
    def lastHearthBeatTime

}
