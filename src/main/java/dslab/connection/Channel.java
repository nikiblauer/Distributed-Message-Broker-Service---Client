package dslab.connection;

import dslab.connection.types.ExchangeType;

import java.io.IOException;
import java.util.function.Consumer;

public class Channel implements IChannel {

    public Channel(String host, int port) {
    }

    @Override
    public boolean connect() throws IOException {
        return false;
    }

    @Override
    public void disconnect() {
        throw new RuntimeException("Not implemented yet.");
    }

    @Override
    public boolean exchangeDeclare(ExchangeType exchangeType, String exchangeName) {
        return false;
    }

    @Override
    public boolean queueBind(String queueName, String bindingKey) {
        return false;
    }

    @Override
    public Thread subscribe(Consumer<String> callback) {
        return null;
    }

    @Override
    public String getFromSubscription() {
        return "";
    }

    @Override
    public boolean publish(String routingKey, String message) {
        return false;
    }
}
