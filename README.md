# SMQP Client
![](header.png)

## What is SMQP Client?

SMQP Client is a simple java based client application, which is used to send and receive messages from a message
broker.
A command line interface is provided to the user to allow easy interaction with the broker. The client is designed to be
used with the corresponding message broker (DSLab Message Broker). The underlying protocol implemented by the client for broker
communication is SMQP (Simple Message Queuing Protocol) which is heavily inspired by the broadly used AMQP (Advanced Message Queuing Protocol).

### Starting the Client Application

To start the client application, you can use the following command:

```bash
# First compile the project with Maven
mvn compile
# Start the client application with the following command where componentId is one of client-0, client-1 or client-2.
mvn exec:java@<componentId>
# You can also combine both commands into one
mvn compile exec:java@<componentId>
```

### Imitate Message Broker Server
The Message Broker Server is only delivered as a mocked component of the test environment. To mimic the behavior of the
Message Broker Server for manual local testing / debugging, you can use for example netcat (nc) to listen on a specific
port and send specific responses to the client. To start a netcat server that listens on `localhost:20000`, you can use the following commands:

#### netcat on Linux and macOS
```bash
# For Linux and macOS
# Open the terminal. The following command starts a netcat server that listens at localhost on port 20000 for incoming connections.
nc -l 20000
```

#### netcat on Windows
```bash
# For Windows first ensure that you have installed netcat (ncat). If not, you can download it from the following link: https://nmap.org/download.html#windows
# Open CMD or PowerShell. The following command starts a netcat server that listens at localhost on port 20000 for incoming connections.
ncat -l 20000
```

After starting the netcat server, you can start the client application with the following command:

```bash
# e.g., compile the project and start client-0
mvn compile exec:java@client-0
```

By entering `channel broker-0` to the CLI, the client application will try connecting to the message broker server (here the netcat server) at `localhost` on port `20000`. The message broker server must respond with the expected response commands
(i.e., *ok SMQP* when the client connects to the message broker server). You can then send response commands to the client and observe the behavior of the client application.


### Local Testing of the Client Application

You can test the client application locally by using the following commands:

```bash
# To execute all tests you can use the following command:
mvn test
# To execute a single test method you can use the following command:
mvn test -Dtest="<testClassName>#<testMethodName>"
# E.g., to execute the test method displays_correct_prompt from the class ClientCLITest you can use the following command:
mvn test -Dtest="ClientCLITest#displays_correct_prompt"
```

