package org.eminux.intime

import groovy.json.JsonBuilder
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPubSub


public class InTimeCommunication {

    private static InTimeCommunication communication = null
    private Jedis jedis = null

    private InTimeCommunication(){
        jedis = new Jedis('127.0.0.1', 6379)
    }

    public void subscribe(String channel){
        jedis.subscribe(new InTimeHelloWorldSubscriber(), channel)
        println("SUBSCRIBED TO "+channel)
    }

    public void subscribe(String channel, JedisPubSub handler){
        jedis.subscribe(handler, channel)
        println("SUBSCRIBED TO "+channel)
    }

    public void sendMessage(String channel, Object message){
        String jsonObject = new JsonBuilder(message).toPrettyString()
        this.jedis.publish(channel, jsonObject)
    }

}
