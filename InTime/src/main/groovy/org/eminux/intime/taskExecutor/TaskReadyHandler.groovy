package org.eminux.intime.taskExecutor

import groovy.json.JsonSlurper
import org.eminux.intime.networkTopology.NetworkNode
import org.eminux.intime.networkTopology.NetworkTopology
import redis.clients.jedis.JedisPubSub

class TaskReadyHandler extends JedisPubSub {

    private NetworkTopology nt = null

    @Override
    void onMessage(String channel, String message) {
        //super.onMessage(channel, message)

        def jsonSlurper = new JsonSlurper()
        def nodeProperties = jsonSlurper.parseText(message)
        NetworkNode node = new NetworkNode()
        node.nome = nodeProperties.nome
        node.ipAddress = nodeProperties.ipAddress
        node.status = NetworkNode.STATUS_READY
        node.lastHearthBeatTime = new Date()
        nt = NetworkTopology.getInstance()
        nt.addNodeToTopology(node)
        println("Ricevuto messaggio READY da "+node.nome)
    }
}
