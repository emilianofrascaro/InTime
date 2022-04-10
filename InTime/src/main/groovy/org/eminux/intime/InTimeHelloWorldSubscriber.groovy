package org.eminux.intime

import redis.clients.jedis.JedisPubSub

class InTimeHelloWorldSubscriber extends JedisPubSub{

    @Override
    public void onSubscribe(String channel, int subscribedChannel){
        println("onSubscribe")
    }

    @Override
    public void onMessage(String channel, String message) {
        println("*********** Message received ***********");
        println(message)
        println("*********** End received message ***********");
    }

}
