package org.eminux.network

import grails.converters.JSON
import org.eminux.intime.networkTopology.NetworkNode
import org.eminux.intime.networkTopology.NetworkTopology

class NetworkController {

    def index() {
        NetworkTopology netTopology = NetworkTopology.getInstance()
        List<NetworkNode> nodeList = netTopology.getListOfNodes()
        nodeList.each{ node ->
            println(node.nome+' -> '+node.status)
        }
        render nodeList as JSON
    }
}
