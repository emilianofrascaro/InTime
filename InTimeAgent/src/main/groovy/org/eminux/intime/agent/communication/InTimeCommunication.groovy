package org.eminux.intime.agent.communication

import org.eminux.intime.agent.configuration.ConfigurationManager
import redis.clients.jedis.Jedis
import groovy.json.*
import redis.clients.jedis.JedisPubSub

class InTimeCommunication{

    private static InTimeCommunication gate = null
    private Jedis jedis = null

    private InTimeCommunication(){
        ConfigurationManager configurationManager = ConfigurationManager.instance
        jedis = new Jedis(configurationManager.redisAddress, 6379)
    }

    public static InTimeCommunication getInstance(){
        if(gate==null){
            gate = new InTimeCommunication()
        }
        return gate
    }

    public static InTimeCommunication getNewInstance(){
        return new InTimeCommunication()
    }

    public void sendMessage(String channel, Object message){
        String jsonObject = new JsonBuilder(message).toPrettyString()
        this.jedis.publish(channel, jsonObject)
    }

    public void subscribe(String channel, JedisPubSub handler){
        this.jedis.subscribe(handler, channel)
        println("SUBSCRIBED TO "+channel)
    }

}
