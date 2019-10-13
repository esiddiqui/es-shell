A simple library to connect and execute commands over SSH. Clone the repository & build artifacts in the mavenLocal to use from other projects

## Publish to mavenLocal
```java
 ./gradlew publishToMavenLocal
```
To use the library, follow examples below.

#### Example 1 :

##### 1.1 Create a request:

````java

  ExecuteRequest request = new ExecuteRequest.ExecuteRequestBuilder()
                    .host("host1.local.es")
                    .userName("user1")
                    .identityFile("/path/to/key.pem")
                    .command("ls -ltr")
                    .command("du -h")
                    .build();

````

##### 1.1 Inject the JSch SSH command executor
For SpringBoot project, you need to add package `com.es.shell` with `@ComponentScan`. 
```java
@ComponentScan({"com.es.shell"})
```
Then use the following to autowire in your classes
```java
  @Autowired
  Shell shell;
```

##### 1.2 or simply create an object yourself
You can simply create the instance of a `Shell` by using the example below. For now only `JSch` is the only supported implementation.

```java
 Shell ssh = new JSchShellExecutor();
```

If running a single command, by default the first in the list of commands in the `ExecuteResult` object, simple call:

```java
 ExecuteResult response = ssh.execute(request);
```

but to run all commands in the request:

```java
 List<ExecuteResults> results = ssh.executeBatch(request);
```