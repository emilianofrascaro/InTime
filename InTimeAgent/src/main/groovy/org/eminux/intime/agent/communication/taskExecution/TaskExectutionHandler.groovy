package org.eminux.intime.agent.communication.taskExecution;

import groovy.json.JsonSlurper;
import org.eminux.intime.agent.communication.InTimeCommunication;
import org.eminux.intime.agent.configuration.ConfigurationManager;
import org.eminux.intime.agent.configuration.NetworkNode
import org.eminux.intime.agent.taskExecution.TaskBean;
import org.eminux.intime.agent.taskExecution.TaskExecutor;
import redis.clients.jedis.JedisPubSub;

import java.text.SimpleDateFormat;

public class TaskExectutionHandler  extends JedisPubSub {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");

    @Override
    public void onMessage(String channel, String message) {
        ConfigurationManager manager = ConfigurationManager.getInstance()
        NetworkNode node = manager.getNode()
        InTimeCommunication communication = InTimeCommunication.getNewInstance()
        System.out.println(sdf.format(new Date())+": Command received")
        TaskExecutor te = new TaskExecutor()
        JsonSlurper jsonSlurper = new JsonSlurper()
        def nodeProperties = jsonSlurper.parseText(message)
        TaskBean tb = new TaskBean()
        tb.command = nodeProperties.command
        tb.processId = nodeProperties.processId
        te.runCommand(tb)

    }

}
