package org.eminux.intime.networkTopology

import org.eminux.intime.InTimeCommunication

class NetworkTopology extends Thread{

    List<NetworkNode> nodeList = null
    static NetworkTopology topology = null

    private NetworkTopology(){
        nodeList = new ArrayList<NetworkNode>()
    }

    public static NetworkTopology getInstance(){
        if(topology==null){
            topology = new NetworkTopology()
        }
        return topology
    }

    @Override
    void run() {
        println("NetworkTopology started")
        (new Thread(){
            @Override
            public void run(){
                InTimeCommunication itc = new InTimeCommunication()
                itc.subscribe('intime.channel.net_topology', new NetworkTopologyCommunicationSubscriber())
            }
        }).start()
        println("Subcribed to "+'intime.channel.net_topology')
        (new Thread(){
            @Override
            public void run(){
                InTimeCommunication itc = new InTimeCommunication()
                itc.subscribe('intime.channel.netpong', new NetworkTopologyCommunicationSubscriber())
            }
        }).start()
        println("Subcribed to "+'intime.channel.netpong')
        println("Starting heartbeat")
        NetworkTopologyHearthBeatManager heartBeat = new NetworkTopologyHearthBeatManager()
        heartBeat.start()
        println("Heartbeat started")
    }

    public void addNodeToTopology(NetworkNode node){
        boolean found = false
        nodeList.each{ nodeL ->
            if(node.nome == nodeL.nome){
                found = true
                nodeL.ipAddress = node.ipAddress
                if(node.status==NetworkNode.STATUS_ONLINE && nodeL.status == NetworkNode.STATUS_READY){
                    nodeL.status = NetworkNode.STATUS_READY
                }else{
                    nodeL.status = node.status
                }
                nodeL.lastHearthBeatTime = node.lastHearthBeatTime
            }
        }
        if(!found){
            nodeList.add(node)
        }
    }

    public void removeNodeToTopology(NetworkNode node){
        boolean found = false
        nodeList.each{ nodeL ->
            if(node.nome == nodeL.nome){
                found = true
                nodeL.status = NetworkNode.STATUS_OFFLINE
            }
        }
    }

    public List<NetworkNode> getListOfNodes(){
        return  this.nodeList
    }

    public NetworkNode getNodeByName(String nome){
        NetworkNode nodeToReturn = null
        nodeList.each{ nodeL ->
            if(nome == nodeL.nome){
                nodeToReturn = nodeL
                return
            }
        }
        return nodeToReturn
    }

}
