package org.eminux.intime.agent.taskExecution;

import java.util.ArrayList;
import java.util.List;

public class TaskBean {

    String command = null;
    Long processId = null;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Long getProcessId() {
        return processId;
    }

    public void setProcessId(Long processId) {
        this.processId = processId;
    }
}
