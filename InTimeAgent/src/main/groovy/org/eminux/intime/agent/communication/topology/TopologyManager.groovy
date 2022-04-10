package org.eminux.intime.agent.communication.topology

import org.eminux.intime.agent.configuration.NetworkNode
import org.eminux.intime.agent.communication.InTimeCommunication
import org.eminux.intime.agent.configuration.ConfigurationManager

class TopologyManager extends Thread{

    InTimeCommunication communication = null

    public TopologyManager(){
        communication = InTimeCommunication.instance
    }

    @Override
    void run() {
        println("Sending startup communication")
        this.sendStartupCommunication()
        println("Startup communication sent")

        communication.subscribe(NetworkPingHandler.NETPING_CHANNEL, new NetworkPingHandler())
        //Thread che non finisce mai altrimenti l'agent terminerebbe chiudendo di fatto il programma
        while(true){
            Thread.sleep(1000)
        }
    }

    private void sendStartupCommunication(){
        ConfigurationManager manager = ConfigurationManager.getInstance()
        NetworkNode node = manager.getNode()
        this.communication.sendMessage('intime.channel.net_topology',node)
    }
}
