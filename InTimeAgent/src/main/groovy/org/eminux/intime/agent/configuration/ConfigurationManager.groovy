package org.eminux.intime.agent.configuration

class ConfigurationManager {

    private static ConfigurationManager manager = null
    private static NetworkNode node = null
    private String redisAddress = null

    private ConfigurationManager(){
    }

    public static ConfigurationManager getInstance(){
        if(manager==null){
            manager = new ConfigurationManager()
        }
        return manager
    }

    public NetworkNode getNode(){
        if(node==null){
            node = new NetworkNode()
            node.ipAddress = "127.0.0.1"
            node.nome = "localhost"
            node.status = NetworkNode.STATUS_ONLINE
        }
        return node
    }

    public void setNetworkNode(NetworkNode node){
        node.status = NetworkNode.STATUS_ONLINE
        this.node = node
    }

    public String getRedisAddress() {
        return redisAddress
    }

    public void setRedisAddress(String redisAddress) {
        this.redisAddress = redisAddress
    }

}
