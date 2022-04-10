package org.eminux.intime.agent.communication.taskExecution;

import org.eminux.intime.agent.communication.InTimeCommunication;
import org.eminux.intime.agent.configuration.ConfigurationManager;
import org.eminux.intime.agent.configuration.NetworkNode;

public class TaskExecutionManager {

    private static String EXECUTION_READY_CHANNEL = "intime.channel.execution.ready";
    private static String EXECUTION_SPECIFIC_NODE_CHANNEL = "intime.channel.execution.task.on.";
    private InTimeCommunication communication = null;
    private ConfigurationManager manager;
    private NetworkNode node;

    public TaskExecutionManager(){
        this.communication = InTimeCommunication.getNewInstance();
        manager = ConfigurationManager.getInstance();
        node = manager.getNode();
    }

    public void init(){
        this.sendStartupCommunication();
        this.subscribeNodeSpecificExecutionChannel();
    }

    private void sendStartupCommunication(){
        node = manager.getNode();
        node.status = NetworkNode.STATUS_READY
        this.communication.sendMessage(EXECUTION_READY_CHANNEL,node);
    }

    private void subscribeNodeSpecificExecutionChannel(){
        this.communication.subscribe(EXECUTION_SPECIFIC_NODE_CHANNEL+node.getNome(), new TaskExectutionHandler());

    }

}
