package org.eminux.intime

import org.eminux.intime.networkTopology.NetworkNode
import org.eminux.intime.networkTopology.NetworkNodeNotReadyException
import org.eminux.intime.networkTopology.NetworkTopology
import org.eminux.intime.taskExecutor.TaskManager
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.quartz.JobExecutionException

import java.text.SimpleDateFormat

class ScheduledTaskJob implements Job {

    @Override
    void execute(JobExecutionContext context) throws JobExecutionException {
        Task.withNewSession{
            String taskId = context.mergedJobDataMap.get('taskId')
            String serverName = context.mergedJobDataMap.get('serverName')
            Task taskToRun = Task.get(Long.parseLong(taskId))
            NetworkTopology nt = NetworkTopology.getInstance()
            NetworkNode server = nt.getNodeByName(serverName.trim())
            if(server.nome == serverName && server.status == NetworkNode.STATUS_READY){
                TaskManager tm = TaskManager.getInstance()
                tm.runCommand(server, taskToRun)
            }else{
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                throw new NetworkNodeNotReadyException(sdf.format(new Date())+": Il server "+server.nome+" non è pronto per eseguire il task "+ taskToRun.descrizione)
            }
            println("INIZIO L'ESECUZIONE DEL TASK")
            println taskId
            println serverName
        }

    }
}
