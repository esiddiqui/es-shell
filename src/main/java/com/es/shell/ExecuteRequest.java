package com.es.shell;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.List;

public class ExecuteRequest {

    private static int DEFAULT_PORT = 22;

    private String host;

    private int port;

    private String userName;

    private String password;

    private String knownHostsFile;

    private String identityFile;

    private boolean hostKeyCheckingEnabled;

    private List<String> commands;


    private ExecuteRequest() {
    }

    private ExecuteRequest(String host, int port, String userName) {
        this(host,port,userName,"./know_hosts","./id_rsa");
    }

    private ExecuteRequest(String host, int port, String userName, String knownHostsFile, String identityFile) {
        this(host,port,userName,knownHostsFile,identityFile,true);
    }

    private ExecuteRequest(String host, int port, String userName, String knownHostsFile, String identityFile, boolean hostKeyCheckingEnabled) {
        this.host = host;
        this.port = port;
        this.userName = userName;
        this.knownHostsFile = knownHostsFile;
        this.identityFile = identityFile;
        this.hostKeyCheckingEnabled = hostKeyCheckingEnabled;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getKnownHostsFile() {
        return knownHostsFile;
    }

    public void setKnownHostsFile(String knownHostsFile) {
        this.knownHostsFile = knownHostsFile;
    }

    public String getIdentityFile() {
        return identityFile;
    }

    public void setIdentityFile(String identityFile) {
        this.identityFile = identityFile;
    }

    public boolean isHostKeyCheckingEnabled() {
        return hostKeyCheckingEnabled;
    }

    public void setHostKeyCheckingEnabled(boolean hostKeyCheckingEnabled) {
        this.hostKeyCheckingEnabled = hostKeyCheckingEnabled;
    }

    public List<String> getCommand() {
        return commands;
    }

    public void setCommand(List<String> commands) {
        this.commands = commands;
    }


    public static class ExecuteRequestBuilder {

        private String host;

        private int port = ExecuteRequest.DEFAULT_PORT;

        private String userName;

        private String password;

        private String knownHostsFile;

        private String identityFile;

        private boolean hostKeyCheckingEnabled;

        private List<String> commands = new ArrayList<>();


        public ExecuteRequestBuilder host(String host) {
            this.host = host;
            return this;
        }

        public ExecuteRequestBuilder port(int port) {
            this.port = port;
            return this;
        }

        public ExecuteRequestBuilder userName(String username) {
            this.userName = username;
            return this;
        }

        public ExecuteRequestBuilder password(String password) {
            this.password = password;
            return this;
        }

        public ExecuteRequestBuilder knownHostFile(String path) {
            this.knownHostsFile = path;
            return this;
        }

        public ExecuteRequestBuilder identityFile(String path) {
            this.identityFile = path;
            return this;
        }

        public ExecuteRequestBuilder hostKeyCheckingEnabled(boolean enabled) {
            this.hostKeyCheckingEnabled = enabled;
            return this;
        }

        public ExecuteRequestBuilder command(String command) {
            Validate.notNull(command,"Command cannot be a null object");
            Validate.isTrue(command.trim().length()>0,"Command cannot be an empty");
            this.commands.add(command);
            return this;
        }

        public ExecuteRequestBuilder commands(List<String> commands) {
            for (String command:commands)
                this.command(command);
            return this;
        }

        public ExecuteRequest build() {

            Validate.notNull(host,"Host name cannot be null");
            Validate.notNull(userName,"User name cannot be null");
            Validate.isTrue(!(this.password!=null && this.identityFile!=null),"Both password & identify file cannot be provided at the same time");

            if (StringUtils.isEmpty(this.knownHostsFile))
                this.knownHostsFile = System.getProperty("user.home") + "/.ssh/known_hosts";

            if (StringUtils.isEmpty(this.identityFile))
                this.identityFile = System.getProperty("user.home") + "/.ssh/id_rsa";

            ExecuteRequest request = new ExecuteRequest();
            request.host = this.host;
            request.port = this.port;
            request.commands = this.commands;
            request.userName = this.userName;
            request.password = this.password;
            request.identityFile =this.identityFile;
            request.knownHostsFile = this.knownHostsFile;
            return request;

        }





    }
}
