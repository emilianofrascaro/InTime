package org.eminux.intime.networkTopology

import groovy.json.JsonSlurper
import redis.clients.jedis.JedisPubSub

class NetworkTopologyCommunicationSubscriber  extends JedisPubSub {

    @Override
    public void onMessage(String channel, String message) {
        //println("*********** Message received from ${channel} ***********");
        //println(message)
        //println("*********** End received message from ${channel} ***********");
        def jsonSlurper = new JsonSlurper()
        def nodeProperties = jsonSlurper.parseText(message)
        NetworkNode node = new NetworkNode()
        node.nome = nodeProperties.nome
        node.ipAddress = nodeProperties.ipAddress
        node.status = nodeProperties.status
        node.lastHearthBeatTime = new Date()
        NetworkTopology topology = NetworkTopology.instance
        topology.addNodeToTopology(node)
    }
}
