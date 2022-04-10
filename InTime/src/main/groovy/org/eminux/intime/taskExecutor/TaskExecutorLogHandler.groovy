package org.eminux.intime.taskExecutor

import grails.util.Holders
import groovy.json.JsonSlurper
import org.springframework.context.ApplicationContext
import redis.clients.jedis.JedisPubSub
import org.eminux.intime.TaskService

class TaskExecutorLogHandler extends JedisPubSub {
    @Override
    void onMessage(String channel, String message) {
        JsonSlurper slurper = new JsonSlurper()
        println("Log: "+message)
        def log = slurper.parseText(message)
        println("Log: "+log)
        TaskLogBean logbean = new TaskLogBean()
        logbean.taskExecution = log.taskExecution
        logbean.tipoOutput = log.tipoOutput
        logbean.log = log.log

        ApplicationContext ctx = Holders.grailsApplication.mainContext
        TaskService service = (TaskService) ctx.getBean("taskService")
        service.saveTaskLoad(logbean)
    }

}
