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
    private final OutputStream out;
    private final PrintStream printStream;
    private Subscription subscription;

    public ClientCLI(IClient client, Config config, InputStream in, OutputStream out) {
        this.client = client;
        this.config = config;
        this.in = in;
        this.out = out;
        this.printStream = new PrintStream(out);
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(in);

        while (true) {
            printPrompt();
            if (scanner.hasNextLine()) {
                String input = scanner.nextLine();
                if(input.endsWith(" ")){
                    printStream.println("error");
                    continue;
                }

                String[] tokens = input.split(" ");
                String command = tokens[0];
                String[] args = Arrays.copyOfRange(tokens, 1, tokens.length);


                if (command.equals("channel")) {
                    if (args.length != 1){
                        printStream.println("error: wrong number of arguments");
                        continue;
                    }

                    channel = createChannel(args[0]);

                }
                else if (command.equals("subscribe")) {
                    if (channel == null){
                        printStream.println("error: channel is null");
                        continue;
                    }
                    if (args.length != 4){
                        printStream.println("error: wrong number of arguments");
                        continue;
                    }

                    String exchangeName = args[0];
                    ExchangeType exchangeType;
                    try {
                        exchangeType = ExchangeType.valueOf(args[1].toUpperCase());
                    } catch (IllegalArgumentException e) {
                        printStream.println("error: wrong exchange type");
                        continue;
                    }
                    String queueName = args[2];
                    String bindingKey = args[3];

                    channel.exchangeDeclare(exchangeType, exchangeName);
                    channel.queueBind(queueName, bindingKey);

                    // Subscribe and print messages received via callback
                    subscription = (Subscription) channel.subscribe(msg -> printStream.println(msg));

                    if (subscription == null){
                        continue;
                    }

                    if (scanner.hasNext()){
                        scanner.nextLine();
                        subscription.interrupt();

                    }
                }
                else if (command.equals("publish")) {
                    if(channel == null){
                        printStream.println("error: channel is null");
                        printStream.flush();
                        continue;
                    }
                    if (args.length != 4){
                        printStream.println("error: wrong number of arguments");
                        printStream.flush();

                        continue;
                    }
                    String exchangeName = args[0];
                    ExchangeType exchangeType = ExchangeType.DEFAULT;
                    boolean changed = false;
                    for (ExchangeType type : ExchangeType.values()){
                        if (type.name().equalsIgnoreCase(args[1])){
                            exchangeType = type;
                            changed = true;
                        }
                    }
                    if (!changed){
                        printStream.println("error: wrong exchange type");
                        continue;
                    }

                    String routingKey = args[2];
                    String msg = args[3];
                    channel.exchangeDeclare(exchangeType, exchangeName);
                    channel.publish(routingKey, msg);
                }
                else if (command.equals("shutdown")) {
                    if(args.length != 0){
                        printStream.println("error: wrong number of arguments");
                    }
                    if (channel != null){
                        channel.disconnect();
                    }

                    client.shutdown();
                    break;
                }
                else {
                    printStream.println("error: Unknown command: " + command);
                }
            }
        }

        scanner.close();
        printStream.close();
        if(channel != null){
            channel.disconnect();
        }
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
