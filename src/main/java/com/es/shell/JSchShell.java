package com.es.shell;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class JSchShell implements Shell {

    private static final String STRICT_HOST_KEY_CHECKING = "StrictHostKeyChecking";

    private static final String YES = "yes";

    private static final String NO = "no";

    private static final String CHANNEL_EXEC = "exec";

    private static final Logger logger = LoggerFactory.getLogger(JSchShell.class);

    @Override
    public ExecuteResult execute(ExecuteRequest request) {
        logger.debug("execute called with {}", request);
        Assert.notNull(request,"Execute Request cannot be null");
        Assert.isTrue(request.getCommand()!=null, "Commands not provided, cannot be null");
        Assert.isTrue(request.getCommand().size()>=0, "At least one command required");
        try {
            Session session = getSession(request);
            session.connect();
            ExecuteResult result = executeCommand(session,request,0);
            session.disconnect();;
            return result;
        } catch (JSchException jshe) {
            jshe.printStackTrace();
            return new ExecuteResult.ExecuteResultBuilder()
                    .commandStatus(ExecuteResult.CommandStatus.SSH_ERROR)
                    .out(Arrays.asList(jshe.getMessage()))
                    .build();
        }
    }

    @Override
    public List<ExecuteResult> executeBatch(ExecuteRequest request) {
        logger.debug("executeBatch called with {}", request);
        Assert.notNull(request,"Execute Request cannot be null");
        Assert.isTrue(request.getCommand()!=null, "Commands not provided, cannot be null");
        Assert.isTrue(request.getCommand().size()>=0, "At least one command required");
        List<ExecuteResult> results = new ArrayList<>();
        try {
            Session session = getSession(request);
            session.connect();
            for (int i=0; i<request.getCommand().size(); i++) {
                results.add(executeCommand(session,request,i));
            }
            session.disconnect();
        } catch (JSchException jshe) {
            jshe.printStackTrace();
            ExecuteResult result = new ExecuteResult.ExecuteResultBuilder()
                    .commandStatus(ExecuteResult.CommandStatus.SSH_ERROR)
                    .out(Arrays.asList(jshe.getMessage()))
                    .build();
            results.add(result);
        }
        return results;
    }


    private Session getSession(ExecuteRequest request) throws JSchException {
        logger.debug("getSession called");
        Assert.notNull(request,"Execute Request cannot be null");
        try {
            String user = request.getUserName();
            String host = request.getHost();
            int port = request.getPort();
            JSch shell = new JSch();
            Session session = shell.getSession(user, host, port);
            if (StringUtils.isEmpty(request.getPassword())) {
                logger.debug("Using Public Key auth");
                session.setConfig(JSchShell.STRICT_HOST_KEY_CHECKING,
                        request.isHostKeyCheckingEnabled() ? YES : NO);
                shell.setKnownHosts(request.getKnownHostsFile());
                shell.addIdentity(request.getIdentityFile());
            } else {
                logger.debug("Using password auth");
                session.setPassword(request.getPassword());
            }
            return session;
        } catch (JSchException jshe) {
            throw jshe;
        }
    }


    private ExecuteResult executeCommand(Session session, ExecuteRequest request, int commandIndex) {
        try {
            ChannelExec channelExec = (ChannelExec) session.openChannel(JSchShell.CHANNEL_EXEC);
            channelExec.setCommand(request.getCommand().get(commandIndex));

            channelExec.setInputStream(null);
            InputStream in = channelExec.getInputStream();
            channelExec.setErrStream(System.err);
            channelExec.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            ExecuteResult.ExecuteResultBuilder resultBuilder = new ExecuteResult.ExecuteResultBuilder();

            while (true) {
                while ((line = reader.readLine()) != null) {
                    resultBuilder.addOutLine(line);
                }
                if (channelExec.isClosed()) {
                    resultBuilder.exitStatus(channelExec.getExitStatus());
                    break;
                }
            }
            channelExec.disconnect();
            return resultBuilder.commandStatus(ExecuteResult.CommandStatus.COMMAND_RAN_SUCCESSFUL).build();
        } catch (JSchException jshe) {
            jshe.printStackTrace();
            return new ExecuteResult.ExecuteResultBuilder()
                    .commandStatus(ExecuteResult.CommandStatus.SSH_ERROR)
                    .out(Arrays.asList(jshe.getMessage()))
                    .build();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return new ExecuteResult.ExecuteResultBuilder()
                    .commandStatus(ExecuteResult.CommandStatus.OTHER_ERROR)
                    .out(Arrays.asList(ioe.getMessage()))
                    .build();
        }
    }


}
