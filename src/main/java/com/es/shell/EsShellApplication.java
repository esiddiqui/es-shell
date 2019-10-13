package com.es.shell;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@SpringBootApplication
@Component
public class EsShellApplication  implements  CommandLineRunner {

    @Autowired
    Shell shell = new JSchShellExecutor();


	public static void main(String[] args) {
	    System.out.println("**********************************");
	    SpringApplication.run(EsShellApplication.class, args);
        System.out.println("**********************************");
	}



	@Override
    public void run(String ... args) {
        String userHome = System.getProperty("user.home");
        String[] biblio  = new String[]{"root","biblio-docker02.pem"};
        String[] ansible = new String[]{"ansible","integration_ansible.pem"};
        HashMap<String,String[]> credentials = new HashMap<String,String[]>(){{
            put("ops-liv-dkr01.bcommons.net",biblio);
//            put("coredocker002.core.prod.bf1.corp.pvt", ansible);
//            put("ops-liv-dkr03.bcommons.net", biblio);
//            put("ops-liv-dkr04.bcommons.net", biblio);
        }};

        for (Object host: credentials.keySet().toArray()) {
            String user = credentials.get(host)[0];
            String pwd = "thedarkknight#";
            String key = credentials.get(host)[1];

            ExecuteRequest request = new ExecuteRequest.ExecuteRequestBuilder()
                    .host(host.toString())
                    .userName(user)
                    .password(pwd)
                    .knownHostFile(userHome + "/.ssh/known_hosts")
                    //.identityFile(userHome + "/ssh-keys/"+key)
                    //.command("docker images")
                    //.command("docker image inspect 1ae77ff7eb81")
                    .command("docker ps")
                    //.command("docker network ls")
                    //.command("docker volume ls")
                    //.command("docker inspect 53af0cc91981")
                    .build();
            List<ExecuteResult> results = this.shell.executeBatch(request);
            for (ExecuteResult result:results) {

                System.out.println("\n*****" + host + "*** ssh_rc=" + result.getReturnStatus() + " rc=" + result.getExitStatus() + "\n");
                for (String line:result.getOut()) {
                    System.out.println(line);
                }
            }

        }

    }


}
