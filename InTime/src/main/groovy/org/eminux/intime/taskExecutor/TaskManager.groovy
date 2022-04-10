package org.eminux.intime.taskExecutor

import grails.util.Holders

import org.eminux.intime.InTimeCommunication
import org.eminux.intime.Task
import org.eminux.intime.TaskExecution
import org.eminux.intime.TaskService
import org.eminux.intime.networkTopology.NetworkNode
import org.eminux.intime.networkTopology.NetworkNodeNotReadyException
import org.eminux.intime.networkTopology.NetworkTopology
import org.eminux.intime.networkTopology.NetworkTopologyCommunicationSubscriber
import org.springframework.context.ApplicationContext

class TaskManager extends Thread {
    private static String EXECUTION_READY_CHANNEL = "intime.channel.execution.ready";
    private static String EXECUTION_SPECIFIC_NODE_CHANNEL = "intime.channel.execution.task.on.";
    private static String EXECUTION_LOG_CHANNEL = "intime.channel.execution.log";

    private static TaskManager taskManagerinstance = null

    private TaskManager(){
        println("Creo l'istanza di TaskManager")

        println("Istanza di TaskManager creata")
    }

    public static TaskManager getInstance(){
        if(taskManagerinstance==null){
            taskManagerinstance = new TaskManager()
        }
        return taskManagerinstance
    }

    public void run(){
        println("Sottoscrivo il canale ready")
        (new Thread(){
            @Override
            public void run(){
                InTimeCommunication communication = new InTimeCommunication()
                communication.subscribe(EXECUTION_READY_CHANNEL, new TaskReadyHandler())
            }
        }).start()
        println("Sottoscrivo il canale ready - OK")
        println("Sottoscrivo il canale log")
        (new Thread(){
            @Override
            public void run(){
                InTimeCommunication communication = new InTimeCommunication()
                communication.subscribe(EXECUTION_LOG_CHANNEL, new TaskExecutorLogHandler())
            }
        }).start()
        println("Sottoscrivo il canale log - OK")
    }

    public void runCommand(NetworkNode node, Task command) throws NetworkNodeNotReadyException{
        NetworkTopology nt = NetworkTopology.instance
        List<NetworkNode> listaNodi = nt.getListOfNodes()
        boolean found = false
        listaNodi.each{ current ->
            if(current.nome == node.nome && current.status == NetworkNode.STATUS_READY){
                found = true
            }
        }

        if(found){
            //Creo la taskExecution
            ApplicationContext ctx = Holders.grailsApplication.mainContext
            TaskService service = (TaskService) ctx.getBean("taskService")
            TaskExecution execution = service.createNewExecution(node, command)

            //Creo il canale di comunicazione
            InTimeCommunication communication = new InTimeCommunication()
            //costruisco il comando
            TaskBean tb = new TaskBean();
            tb.command = command.command
            tb.processId = execution.id
            //invio il comando
            communication.sendMessage(EXECUTION_SPECIFIC_NODE_CHANNEL+node.nome, tb)
        }else{
            println("Richiesto comando su "+node.nome+" ma il nodo non e' pronto o e' offline")
            throw new NetworkNodeNotReadyException("Richiesto comando su "+node.nome+" ma il nodo non e' pronto o e' offline");
        }
    }

}
