package org.eminux.intime

import grails.converters.JSON
import org.eminux.intime.beans.SchedulazioneBeanWeb
import org.eminux.intime.beans.TaskBeanWeb
import org.eminux.intime.beans.TaskExecutionBeanWeb
import org.eminux.intime.beans.TaskExecutionLogBeanWeb
import org.eminux.intime.networkTopology.NetworkNode
import org.eminux.intime.networkTopology.NetworkTopology
import org.eminux.intime.taskExecutor.TaskExecutionBean
import org.eminux.intime.taskExecutor.TaskManager
import org.quartz.CronExpression
import org.quartz.CronScheduleBuilder
import org.quartz.CronTrigger
import org.quartz.JobBuilder
import org.quartz.JobDetail
import org.quartz.TriggerBuilder
import org.quartz.TriggerKey

import java.text.SimpleDateFormat

class CommandController {

    static namespace = 'api'

    def quartzScheduler

    static allowedMethods = [index: 'GET', sendCommand: 'GET', saveTask: 'POST', listTask: 'GET']

    def index() { }

    def saveTask(){
        def parameters = request.JSON
        println(parameters)
        boolean salvato = false
        Task.withTransaction{
            Task task = new Task()
            task.command = parameters["comando"]
            task.descrizione = parameters["descrizione"]
            salvato = task.save(flush:true, failOnError:true)
        }
        if(salvato){
            render "OK"
        }else{
            render "KO"
        }

    }

    def listTask(){
        def listaTask = Task.findAllByStato(Task.STATO_ATTIVO)
        List<TaskBeanWeb> listaTaskWeb = new ArrayList<>()
        listaTask.each{ task ->
            TaskBeanWeb twb = new TaskBeanWeb()
            twb.id = task.id
            twb.command = task.command
            twb.descrizione = task.descrizione
            def query = Schedulazione.where{
                taskToExecute.id == task.id
                status.codice == StatoSchedulazione.SCHEDULAZIONE_ATTIVA
            }
            List<Schedulazione> schedulazioni = query.list()
            twb.numSchedulazioniAttive = schedulazioni.size()
            println(twb.numSchedulazioniAttive)
            listaTaskWeb.add(twb)
        }
        render listaTaskWeb as JSON
    }

    def runNowTask(){
        def parameters = request.JSON
        println(parameters)
        def idTask = parameters.idComando
        def server = parameters.server
        Task taskToRun = Task.get(idTask)
        NetworkTopology nt = NetworkTopology.getInstance()
        NetworkNode node = nt.getNodeByName(server)
        TaskManager tm = TaskManager.getInstance()
        tm.runCommand(node, taskToRun)
        render "OK"
    }

    def taskExecutionForToday(){
        GregorianCalendar cal = GregorianCalendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        Date today = cal.getTime()
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        println(sdf.format(today))
        def query = TaskExecution.where{
            dataEsecuzione >= today
        }

        List<TaskExecution> listaTask = query.list()
        List<TaskExecutionBean> listaBean = new ArrayList<>()
        listaTask.each{ exec->
            TaskExecutionBean executionBean = new TaskExecutionBean()
            executionBean.taskName = exec.task.descrizione
            executionBean.taskId = exec.task.id
            executionBean.taskExecutionId = exec.id
            executionBean.taskExecutionDate = sdf.format(exec.dataEsecuzione)
            executionBean.taskExecutionStatus = exec.stato
            executionBean.taskExecutionServer = exec.server
            listaBean.add(executionBean)
        }
        println(listaBean.size())
        render listaBean as JSON
    }

    def scheduleTask(){
        def parameters = request.JSON
        def serverName = parameters['serverName']
        def taskId = parameters['taskId']
        def crontab = parameters['crontab']
        def parametri = [serverName: serverName, taskId: taskId]
        Schedulazione.withTransaction { tran ->
            try {
                def statoSchedulazione = StatoSchedulazione.findByCodice(StatoSchedulazione.SCHEDULAZIONE_ATTIVA)
                def task = Task.get(taskId)
                def schedulazione = new Schedulazione()
                schedulazione.taskToExecute = task
                schedulazione.status = statoSchedulazione
                schedulazione.serverName = serverName
                schedulazione.cronExpression = crontab.trim()
                schedulazione.save(flush: true, failOnError: true)

                JobDetail job = JobBuilder.newJob(ScheduledTaskJob.class).withIdentity("schedulazione_"+schedulazione.id, "task_"+task.id).build();
                job.getJobDataMap().put("serverName", serverName)
                job.getJobDataMap().put("taskId", taskId)
                println(crontab.trim())
                boolean isValidExpression = CronExpression.isValidExpression(crontab.trim())
                println("E' valida? " + isValidExpression)
                println(quartzScheduler.toString())
                CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity("schedulazione_"+schedulazione.id, "task_"+task.id).withSchedule(CronScheduleBuilder.cronSchedule(crontab.trim())).build();
                quartzScheduler.scheduleJob(job, trigger)
                //ScheduledTaskJob.schedule(trigger, parametri)
            } catch (Throwable t) {
                tran.setRollbackOnly()
                t.printStackTrace()
                render 'KO'
            }
        }
        render 'OK'
    }

    def listOfSchedulePerTask(){
        def parameters = request.JSON
        def taskId = parameters['taskId']
        println("TASKID: "+taskId)
        def query = Schedulazione.where{
            taskToExecute.id == taskId
        }
        List<Schedulazione> schedulazioni = query.list()
        println("Numero schedulazioni: "+schedulazioni.size())
        List<SchedulazioneBeanWeb> listaSchedulazioni = new ArrayList<>()
        schedulazioni.each{schedulazione ->
            if(schedulazione.status.codice!=StatoSchedulazione.SCHEDULAZIONE_ELIMINATA) {
                def bean = new SchedulazioneBeanWeb()
                bean.taskId = schedulazione.taskToExecute.id
                bean.cronExpression = schedulazione.cronExpression
                bean.serverName = schedulazione.serverName
                bean.status = schedulazione.status.descrizione
                bean.statusCode = schedulazione.status.codice
                bean.schedulazioneId = schedulazione.id
                listaSchedulazioni.add(bean)
            }
        }
        render listaSchedulazioni as JSON
    }

    def unscheduleTask(){
        def parameters = request.JSON
        def schedulazioneId = parameters['schedulazioneId']
        def schedulazione = Schedulazione.get(schedulazioneId)
        Schedulazione.withTransaction{ tran ->
            try{
                schedulazione.status = StatoSchedulazione.findByCodice(StatoSchedulazione.SCHEDULAZIONE_NON_ATTIVA)
                println(schedulazione.status)
                schedulazione.save(flush: true, failOnError: true)
                TriggerKey trigger = TriggerKey.triggerKey('schedulazione_'+schedulazione.id, 'task_'+schedulazione.taskToExecute.id)
                boolean schedulato = quartzScheduler.unscheduleJob(trigger)
                println('SCHEDULATO: '+schedulato);
            }catch(Throwable t){
                t.printStackTrace()
                tran.setRollbackOnly()
                render 'KO'
            }
            render 'OK'
        }
    }

    def rescheduleTask(){
        def parameters = request.JSON
        def schedulazioneId = parameters['schedulazioneId']
        def schedulazione = Schedulazione.get(schedulazioneId)
        Schedulazione.withTransaction { tran ->
            try {
                schedulazione.status = StatoSchedulazione.findByCodice(StatoSchedulazione.SCHEDULAZIONE_ATTIVA)
                println(schedulazione.status)
                schedulazione.save(flush: true, failOnError: true)
                JobDetail job = JobBuilder.newJob(ScheduledTaskJob.class).withIdentity("schedulazione_"+schedulazione.id, "task_"+schedulazione.taskToExecute.id).build();
                job.getJobDataMap().put("serverName", schedulazione.serverName)
                job.getJobDataMap().put("taskId", schedulazione.taskToExecute.id)
                boolean isValidExpression = CronExpression.isValidExpression("0 0 2 * * ?")
                println("E' valida? " + isValidExpression)
                println(quartzScheduler.toString())
                CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity("schedulazione_"+schedulazione.id, "task_"+schedulazione.taskToExecute.id).withSchedule(CronScheduleBuilder.cronSchedule(schedulazione.cronExpression.trim())).build();
                quartzScheduler.scheduleJob(job, trigger)
            } catch (Throwable t) {
                tran.setRollbackOnly()
                t.printStackTrace()
                render 'KO'
            }
            render 'OK'
        }

    }

    def deleteSchedulazione(){
        def parameters = request.JSON
        def schedulazioneId = parameters['schedulazioneId']
        def schedulazione = Schedulazione.get(schedulazioneId)
        Schedulazione.withTransaction { tran ->
            try {
                schedulazione.status = StatoSchedulazione.findByCodice(StatoSchedulazione.SCHEDULAZIONE_ELIMINATA)
                println(schedulazione.status)
                schedulazione.save(flush: true, failOnError: true)

            } catch (Throwable t) {
                tran.setRollbackOnly()
                t.printStackTrace()
                render 'KO'
            }
            render 'OK'
        }
    }

    def taskLogForExecution(){
        def parameters = request.JSON
        def esecuzioneId = parameters['executionId']
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        println(esecuzioneId)
        TaskExecutionBeanWeb exec = new TaskExecutionBeanWeb()
        TaskExecution execution = TaskExecution.get(esecuzioneId)
        exec.nomeServer = execution.server
        exec.nomeTask = execution.task.descrizione
        exec.orarioEsecuzione = sdf.format(execution.dataEsecuzione)
        List<TaskExecutionLogBeanWeb> listaLog = new ArrayList<>()
        List<TaskLog> listaLogDb = TaskLog.findAllByExecution(execution)
        println("ELEMENTI LOG TROVATI "+listaLogDb.size())
        exec.listaLog = listaLog
        listaLogDb.each{log ->
            TaskExecutionLogBeanWeb te = new TaskExecutionLogBeanWeb()
            te.id = log.id
            te.tipoLog = log.tipoLog
            te.logMessage = log.logMessage
            te.oraLog = sdf.format(log.oraLog)
            listaLog.add(te)
        }
        render exec as JSON
    }

    def archiviaTask(){
        def parameters = request.JSON
        def taskId = parameters['taskId']
        Task.withTransaction{ tran ->
            try {
                Task task = Task.get(taskId)
                task.stato = Task.STATO_ARCHIVIATO
                task.save(flush: true, failOnError: true)
            }catch(Throwable tr){
                tr.printStackTrace()
                tran.setRollbackOnly()
            }
        }
        render 'OK'
    }
}
