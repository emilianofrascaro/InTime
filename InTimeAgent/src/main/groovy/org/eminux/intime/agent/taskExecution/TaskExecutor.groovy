package org.eminux.intime.agent.taskExecution;

import org.eminux.intime.agent.communication.InTimeCommunication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.io.IOUtils;

public class TaskExecutor {

    InTimeCommunication communication = null;
    private static String EXECUTION_LOG_CHANNEL = "intime.channel.execution.log";

    public TaskExecutor(){

    }

    public void runCommand(TaskBean taskBean) {
        String status = "OK";
        try {
            Runtime runtime = Runtime.getRuntime();
            String command = taskBean.command;
            String[] comando = command.replace("\"", "").split(" ");
            for(int i=0; i<comando.length; i++){
                System.out.println(i+" -> "+comando[i]);
            }
            Process process = runtime.exec(comando);
            InputStream inputStream = process.getInputStream();
            InputStream errorStream = process.getErrorStream();
            int exit = process.waitFor();
            System.out.println("Il comando '"+command+"' e' terminato");
            System.out.println("ExitCode -> "+exit);
            String output = IOUtils.toString(inputStream, "UTF-8");

            if(output.size()>0){
                System.out.println("OUTPUT: "+output);
                InTimeCommunication communication = InTimeCommunication.getNewInstance()
                TaskLogBean outputLog = new TaskLogBean()
                outputLog.taskExecution = taskBean.processId
                outputLog.tipoOutput = "OUTPUT"
                outputLog.log = output
                communication.sendMessage(EXECUTION_LOG_CHANNEL, outputLog)
            }
            String error = IOUtils.toString(errorStream, "UTF-8");
            if(error.size()>0){
                InTimeCommunication communication = InTimeCommunication.getNewInstance()
                System.out.println("ERROR: "+error);
                TaskLogBean errorLog = new TaskLogBean()
                errorLog.taskExecution = taskBean.processId
                errorLog.tipoOutput = "ERROR"
                errorLog.log = error
                communication.sendMessage(EXECUTION_LOG_CHANNEL, errorLog)
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            status = "ERROR"
        } finally {
            TaskLogBean endLog = new TaskLogBean()
            endLog.taskExecution = taskBean.processId
            endLog.tipoOutput = "END"
            endLog.log = status
            InTimeCommunication communication = InTimeCommunication.getNewInstance()
            communication.sendMessage(EXECUTION_LOG_CHANNEL, endLog)
        }

    }



}
