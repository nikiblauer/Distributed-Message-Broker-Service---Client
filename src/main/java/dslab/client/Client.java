package dslab.client;

import dslab.ComponentFactory;
import dslab.cli.ClientCLI;

import java.io.InputStream;
import java.io.OutputStream;

public class Client implements IClient {

    public Client(String componentId, InputStream in, OutputStream out) {
    }

    @Override
    public void run() {
        throw new RuntimeException("Not implemented yet.");
    }

    @Override
    public String getComponentId() {
        return "";
    }

    @Override
    public void shutdown() {
        throw new RuntimeException("Not implemented yet.");
    }

    /**
     * Creates a new {@link Client} and runs it.
     * Standard input, output and error streams are passed to the client which are then used by the {@link ClientCLI}.
     *
     * @param args the client config filename found in classpath resources without the file extension
     */
    public static void main(String[] args) {
        ComponentFactory.createClient(args[0], System.in, System.out).run();
    }
}
