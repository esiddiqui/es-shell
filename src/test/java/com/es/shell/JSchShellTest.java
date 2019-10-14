package com.es.shell;

import org.junit.Assert;
import org.junit.Test;
import java.util.HashMap;


public class JSchShellTest {

    private Shell shell = new JSchShell();

    @Test
    public void testBasicConnection() {
        String userHome = System.getProperty("user.home");
        String hostName = "felb001.core.int.bf1.corp.pvt";
        ExecuteRequest request = new ExecuteRequest.ExecuteRequestBuilder()
                .host(hostName)
                .userName("ansible")
                .knownHostFile(userHome + "/.ssh/known_hosts")
                .identityFile(userHome + "/ssh-keys/integration_ansible.pem")
                .command("hostname -f")
                .build();
        Assert.assertNotNull(request);
        ExecuteResult result = this.shell.execute(request);
        Assert.assertEquals(ExecuteResult.CommandStatus.COMMAND_RAN_SUCCESSFUL, result.getReturnStatus());
        Assert.assertEquals(0,result.getExitStatus());
        Assert.assertEquals(hostName, result.getOut().get(0).trim());
    }


    @Test(expected = Exception.class)
    public void testErrorWithBothPasswordAndKeySupplied() {
        String userHome = System.getProperty("user.home");
        String hostName = "hostname.local.es";
        ExecuteRequest request = new ExecuteRequest.ExecuteRequestBuilder()
                .host(hostName)
                .userName("user")
                .knownHostFile(userHome + "/.ssh/known_hosts")
                .identityFile(userHome + "/ssh-keys/hostname_local.pem")
                .password("somepassword")
                .command("hostname -f")
                .build();
    }


    @Test
    public void testCommandOnMultipleHosts() {
        String userHome = System.getProperty("user.home");
        String[] biblio  = new String[]{"root","biblio-docker02.pem"};
        String[] ansible = new String[]{"ansible","integration_ansible.pem"};
        HashMap<String,String[]> credentials = new HashMap<String,String[]>(){{
            put("ops-liv-dkr01.bcommons.net",biblio);
            put("coredocker002.core.prod.bf1.corp.pvt", ansible);
            put("ops-liv-dkr03.bcommons.net", biblio);
            put("ops-liv-dkr04.bcommons.net", biblio);
        }};

        for (Object host: credentials.keySet().toArray()) {
            String user = credentials.get(host)[0];
            String key = credentials.get(host)[1];

            ExecuteRequest request = new ExecuteRequest.ExecuteRequestBuilder()
                    .host(host.toString())
                    .userName(user)
                    .knownHostFile(userHome + "/.ssh/known_hosts")
                    .identityFile(userHome + "/ssh-keys/" + key)
                    .command("docker ps")
                    .build();
            ExecuteResult result = this.shell.execute(request);
            System.out.println("*****" + host + "***");
            for (String line : result.getOut()) {
                System.out.println(line);
            }
            Assert.assertEquals(ExecuteResult.CommandStatus.COMMAND_RAN_SUCCESSFUL, result.getReturnStatus());
            Assert.assertEquals(0, result.getExitStatus());
        }
    }


    @Test
    public void testCommandOnSingleHost() {
        String userHome = System.getProperty("user.home");
        String hostName = "felb001.core.int.bf1.corp.pvt";
        ExecuteRequest request = new ExecuteRequest.ExecuteRequestBuilder()
                .host(hostName)
                .userName("ansible")
                .knownHostFile(userHome + "/.ssh/known_hosts")
                .identityFile(userHome + "/ssh-keys/integration_ansible.pem")
                .command("hostname -f")
                .build();
        ExecuteResult result = this.shell.execute(request);
        Assert.assertEquals(ExecuteResult.CommandStatus.COMMAND_RAN_SUCCESSFUL, result.getReturnStatus());
        Assert.assertEquals(0,result.getExitStatus());
        Assert.assertEquals(hostName, result.getOut().get(0).trim());
    }

}

