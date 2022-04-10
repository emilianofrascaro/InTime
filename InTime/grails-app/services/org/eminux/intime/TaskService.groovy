package org.eminux.intime

import grails.gorm.transactions.Transactional
import org.eminux.intime.networkTopology.NetworkNode
import org.eminux.intime.taskExecutor.TaskLogBean


@Transactional
class TaskService {

    def saveTaskLoad(TaskLogBean bean) {
        TaskExecution te = TaskExecution.get(bean.taskExecution)
        TaskLog log = new TaskLog()
        log.execution = te
        log.tipoLog = bean.tipoOutput
        log.logMessage = bean.log
        log.save(flush:true, failOnError:true)
        if(bean.tipoOutput=="END" && bean.log!="ERROR"){
            te.stato = "END"
            te.save(flush:true, failOnError: true)
        }else if(bean.tipoOutput=="END" && bean.log=="ERROR"){
            te.stato = "ERROR"
            te.save(flush:true, failOnError: true)
        }
    }

    def createNewExecution(NetworkNode nodo, Task task){
        TaskExecution execution = new TaskExecution()
        execution.server = nodo.nome
        execution.stato = "IN_CORSO"
        execution.task = task
        execution.save(flush:true, failOnError:true)
        return execution
    }
}
