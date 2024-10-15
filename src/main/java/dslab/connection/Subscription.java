package dslab.connection;

import java.io.IOException;
import java.util.function.Consumer;

public class Subscription extends Thread {

    private final Channel channel;
    private final Consumer<String> callback;
    public Subscription(IChannel channel, Consumer<String> callback) {
        this.channel = (Channel) channel;
        this.callback = callback;
    }

    @Override
    public void run() {
        /*while (!isInterrupted()){
            String msg = channel.getFromSubscription();
            if (msg != null){
                callback.accept(msg);
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {

            }

        }

         */
        while (!isInterrupted()){
            String msg = channel.getFromSubscription();
            if (msg != null){
                callback.accept(msg);
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                break;
            }

        }
    }
}

