package dslab.connection;

import java.util.function.Consumer;

public class Subscription extends Thread {

    public Subscription(IChannel channel, Consumer<String> callback) {
    }

    @Override
    public void run() {
        throw new RuntimeException("Not implemented yet.");
    }
}

