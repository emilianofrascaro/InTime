package org.eminux.intime.agent.utils

import java.text.SimpleDateFormat

class Logger {

    public static Integer INFO = 1
    public static Integer WARNING = 2
    public static Integer ERROR = 3

    private boolean autoRotate = false
    private File logFolder = null
    private static Logger loggerInstance = null
    private Date lastLogDate = null
    private File logFile = null

    private Logger(){
    }

    public static Logger getInstance(){
        if(this.loggerInstance == null){
            this.loggerInstance = new Logger()
        }
        return this.loggerInstance
    }


    /**
     * With this method you will set the folder in which the log file will be created.
     * If the folder doesn't exists, the logger create it.
     *
     * @param pathToOutputFolder the folder in which the log file will be created
     */
    public void setOutputFolder(String pathToOutputFolder){
        File logFolder = new File(pathToOutputFolder)
        if(!logFolder.exists() || !logFolder.isDirectory()){
            boolean created = logFolder.mkdirs()
            if(created){
                this.logFolder = logFolder
            }else{
                throw new LogFolderNotCreatedException("ERROR: Log Folder not created!")
            }
        }else{
            this.logFolder = logFolder
        }
    }

    /**
     * Set the autoRotate flag. If true, you will have a file for a day in which the Agent
     * logs. If false the Agent will write all logs in a single file.
     *
     * @param autoRotate If true, you will have a file for a day in which the Agent
     * logs. If false the Agent will write all logs in a single file.
     */
    public void setAutoRotate(boolean autoRotate){
        this.autoRotate = autoRotate
    }

    private File getFileToLog(){
        File logFile = null
        if(logFolder==null){
            throw new LogFolderNotFoundException("You have to set the output folder first!")
        }
        if(!this.autoRotate){
            if(this.logFile==null){
                this.logFile = new File(logFolder,'InTimeLog.log')
            }
            logFile = this.logFile
        }else{
            boolean newLog = false
            Calendar calendar = new GregorianCalendar()
            Date currentLogDate = calendar.getTime()
            Integer currentLogYear = calendar.get(Calendar.YEAR)
            Integer currentLogMonth = calendar.get(Calendar.MONTH)
            Integer currentLogDay = calendar.get(Calendar.DAY_OF_MONTH)
            if(lastLogDate == null){
                newLog = true
            }else{
                Calendar lastLogCalendar = lastLogDate.toCalendar()
                Integer lastLogYear = lastLogCalendar.get(Calendar.YEAR)
                Integer lastLogMonth = lastLogCalendar.get(Calendar.MONTH)
                Integer lastLogDay = lastLogCalendar.get(Calendar.DAY_OF_MONTH)
                if(lastLogYear < currentLogYear || lastLogMonth < currentLogMonth || lastLogDay < currentLogDay){
                    newLog = true
                }
            }
            if(newLog){
                if(logFolder==null){
                    throw new LogFolderNotFoundException("You have to set the output folder first!")
                }else{
                    SimpleDateFormat sdf = new SimpleDateFormat('yyyyMMdd')
                    String logFileName = 'InTimeAgentLog_'+sdf.format(currentLogDate)+'.log'
                    logFile = new File(logFolder, logFileName)
                    this.logFile = logFile
                }
            }else{
                logFile = this.logFile
            }
        }
        return logFile
    }

    public void log(Integer logLevel, String message){
        String level = null
        switch (logLevel){
            case INFO:
                level = "INFO"
                break
            case WARNING:
                level = "WARNING"
                break
            case ERROR:
                level = "ERROR"
                break
            default:
                level = "UNKNOWN"
        }
        File logFile = this.getFileToLog()
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS")
        logFile << level+': '+sdf.format(new Date())+" -> "+message + '\n'
    }

}
