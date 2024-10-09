package dslab.cli;

import dslab.client.IClient;
import dslab.config.Config;
import dslab.connection.Channel;

import java.io.InputStream;
import java.io.OutputStream;

public class ClientCLI implements IClientCLI {

    private Channel channel;

    public ClientCLI(IClient client, Config config, InputStream in, OutputStream out) {
    }

    @Override
    public void run() {
    }

    @Override
    public void printPrompt() {
        throw new RuntimeException("Not implemented yet.");
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
        return null;
    }
}
