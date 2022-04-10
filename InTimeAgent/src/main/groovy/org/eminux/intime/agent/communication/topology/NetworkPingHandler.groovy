package org.eminux.intime.agent.communication.topology

import org.eminux.intime.agent.communication.InTimeCommunication
import org.eminux.intime.agent.configuration.ConfigurationManager
import org.eminux.intime.agent.configuration.NetworkNode
import redis.clients.jedis.JedisPubSub

import java.text.SimpleDateFormat

class NetworkPingHandler extends JedisPubSub {

    public static String NETPING_CHANNEL = 'intime.channel.netping'
    private SimpleDateFormat sdf = new SimpleDateFormat('yyyy-MM-dd HH:mm:ss.S')

    @Override
    void onMessage(String channel, String message) {
        //super.onMessage(channel, message)

        //println(sdf.format(new Date())+": NETPING message received")
        ConfigurationManager manager = ConfigurationManager.getInstance()
        NetworkNode node = manager.getNode()
        InTimeCommunication communication = InTimeCommunication.getNewInstance()
        //println(node.status)
        communication.sendMessage('intime.channel.netpong', node)
        //println(sdf.format(new Date())+": NETPONG message sent")
    }
}
