package dslab.cli;

import dslab.client.Client;
import dslab.client.IClient;
import dslab.config.Config;
import dslab.connection.Channel;
import dslab.connection.Subscription;
import dslab.connection.types.ExchangeType;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Scanner;

public class ClientCLI implements IClientCLI {

    private Channel channel;
    private final IClient client;
    private final Config config;
    private final InputStream in;
    private final PrintStream printStream;

    public ClientCLI(IClient client, Config config, InputStream in, OutputStream out) {
        this.client = client;
        this.config = config;
        this.in = in;
        this.printStream = new PrintStream(out);
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(in);

        while (true) {
            printPrompt();

            if (!scanner.hasNextLine()) {
                continue;
            }

            String input = scanner.nextLine();
            if (input.isEmpty()) {
                printError("Empty input");
                continue;
            }

            if (input.endsWith(" ")) {
                printError("Input cannot end with a space");
                continue;
            }

            String[] tokens = input.split(" ");
            String command = tokens[0];
            String[] args = Arrays.copyOfRange(tokens, 1, tokens.length);

            switch (command.toLowerCase()) {
                case "channel":
                    handleChannelCommand(args);
                    break;
                case "subscribe":
                    handleSubscribeCommand(args, scanner);
                    break;
                case "publish":
                    handlePublishCommand(args);
                    break;
                case "shutdown":
                    handleShutdownCommand(args);
                    return;
                default:
                    printError("Unknown command: " + command);
                    break;
            }
        }
    }

    private void handleChannelCommand(String[] args) {
        if (args.length != 1) {
            printError("wrong number of arguments for channel");
            return;
        }
        channel = createChannel(args[0]);
    }

    private void handleSubscribeCommand(String[] args, Scanner scanner) {
        if (channel == null) {
            printError("channel is null");
            return;
        }

        if (args.length != 4) {
            printError("wrong number of arguments for subscribe");
            return;
        }

        String exchangeName = args[0];
        ExchangeType exchangeType = parseExchangeType(args[1]);
        if (exchangeType == null) {
            printError("wrong exchange type");
            return;
        }

        String queueName = args[2];
        String bindingKey = args[3];

        channel.exchangeDeclare(exchangeType, exchangeName);
        channel.queueBind(queueName, bindingKey);

        Subscription subscription = (Subscription) channel.subscribe(printStream::println);

        if (subscription == null) {
            return;
        }

        if (scanner.hasNext()) {
            scanner.nextLine();
            subscription.interrupt();
        }
    }

    private void handlePublishCommand(String[] args) {
        if (channel == null) {
            printError("channel is null");
            return;
        }

        if (args.length != 4) {
            printError("wrong number of arguments for publish");
            return;
        }

        String exchangeName = args[0];
        ExchangeType exchangeType = parseExchangeType(args[1]);
        if (exchangeType == null) {
            printError("wrong exchange type");
            return;
        }

        String routingKey = args[2];
        String msg = args[3];

        channel.exchangeDeclare(exchangeType, exchangeName);
        channel.publish(routingKey, msg);
    }

    private void handleShutdownCommand(String[] args) {
        if (args.length != 0) {
            printError("wrong number of arguments for shutdown");
            return;
        }

        if (channel != null) {
            channel.disconnect();
        }

        client.shutdown();
    }

    private ExchangeType parseExchangeType(String typeStr) {
        for (ExchangeType type : ExchangeType.values()) {
            if (type.name().equalsIgnoreCase(typeStr)) {
                return type;
            }
        }
        return null;
    }

    private void printError(String errorMsg) {
        printStream.println("error: " + errorMsg);
        printStream.flush();
    }

    @Override
    public void printPrompt() {
        printStream.print(client.getComponentId() + "> ");
    }

    /**
     * A channel is an instance which is used to multiplex connections on a single TCP connection.
     * Please read the documentation of the {@link Channel} class for more information.
     * Attention should be paid if the channel is already connected to a broker, it should be disconnected first.
     *
     * @param broker the broker to which the channel should be created
     * @return the channel to which a connection is established with the specified broker
     */
    private Channel createChannel(String broker) {
        if (channel != null){
            channel.disconnect();
        }

        String hostKey = broker + ".host";
        String portKey = broker + ".port";

        if (!config.containsKey(hostKey) || !config.containsKey(portKey)) {
            printStream.println("error: could not create channel");
            return null;
        }
        String host = config.getString(broker + ".host");
        int port = config.getInt(broker + ".port");

        Channel c = new Channel(host, port);
        if (!c.connect()){
            printStream.println("error: could not connect to channel");
            return null;
        }

        return c;
    }
}
