package org.eminux.intime.networkTopology

import org.eminux.intime.InTimeCommunication

class NetworkTopologyHearthBeatManager extends Thread{

    public static String NETPING_CHANNEL = 'intime.channel.netping'
    private static Long HEARTBEAT_INTERVAL = 10000

    public void run(){
        println("Heartbeat started")
        InTimeCommunication communication = new InTimeCommunication()
        while(true){
            communication.sendMessage(NETPING_CHANNEL, {})
            List<NetworkNode> nodeList = NetworkTopology.instance.listOfNodes
            nodeList.each{ node ->
                Date now= new Date()
                //println(now.getTime()-node.lastHearthBeatTime.getTime())
                if(now.getTime() - node.lastHearthBeatTime.getTime() > HEARTBEAT_INTERVAL){
                    node.status = NetworkNode.STATUS_OFFLINE
                }
            }
            Thread.sleep(HEARTBEAT_INTERVAL)
        }
    }
}
