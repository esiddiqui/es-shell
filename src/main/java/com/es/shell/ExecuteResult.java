package com.es.shell;

//import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ExecuteResult {

    private List<String> out= new ArrayList<>();

    private CommandStatus commandStatus;

    private int exitStatus;

    private ExecuteResult(CommandStatus status, int rc, List<String> out) {
        this.out = out;
        this.exitStatus = rc;
        this.commandStatus = status;
    }


    public List<String> getOut() {
        return out;
    }

    public void setOut(List<String> out) {
        this.out = out;
    }

    public CommandStatus getReturnStatus() {
        return commandStatus;
    }

    public void setCommandStatus(CommandStatus commandStatus) {
        this.commandStatus = commandStatus;
    }

    public int getExitStatus() {
        return exitStatus;
    }

    public void setExitStatus(int returnCode) {
        this.exitStatus = returnCode;
    }

    public enum CommandStatus {
        SSH_ERROR(0), OTHER_ERROR(1), COMMAND_RAN_WITH_ERROR(2), COMMAND_RAN_SUCCESSFUL(3);

        private int value = 0;

         CommandStatus(int value) {
            this.value = value;
        }
    }

    public static class ExecuteResultBuilder {

        private List<String> out= new ArrayList<>();

        private CommandStatus commandStatus;

        private int exitStatus;

        public ExecuteResultBuilder addOutLine(String line) {
            if (line!=null && !line.isEmpty())
              this.out.add(line);
            return this;
        }

        public ExecuteResultBuilder out(List<String> out) {
            this.out = out;
            return this;
        }

        public ExecuteResultBuilder exitStatus(int rc) {
            this.exitStatus = rc;
            return this;
        }

        public ExecuteResultBuilder commandStatus(CommandStatus commandStatus) {
            this.commandStatus = commandStatus;
            return this;
        }

        public ExecuteResult build() {
            return  new ExecuteResult(commandStatus,exitStatus,out);
        }

    }
}
