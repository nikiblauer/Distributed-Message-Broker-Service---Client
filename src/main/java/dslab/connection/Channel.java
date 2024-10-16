package dslab.connection;

import dslab.connection.types.ExchangeType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.Buffer;
import java.util.function.Consumer;

public class Channel implements IChannel {

    private final String host;
    private final int port;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    ExchangeType exchangeType;
    String exchangeName;

    public Channel(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public boolean connect() {
        try {
            socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String answer = in.readLine();
            if (!answer.equals("ok SMQP")){
                closeSocket();
                return false;
            }
            return true;
        } catch (IOException e2) {
            return false;
        }
    }


    private void closeSocket() {
        try{
            if(socket != null){
                socket.close();
            }

        } catch (IOException ignored){

        }
    }

    @Override
    public void disconnect() {
        try{
            out.println("exit");
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String answer = in.readLine();

            if (!answer.equals("ok bye")){
                return;
            }


            closeSocket();
        } catch (IOException ignored) {

        }

    }

    @Override
    public boolean exchangeDeclare(ExchangeType exchangeType, String exchangeName) {
        this.exchangeType = exchangeType;
        this.exchangeName = exchangeName;

        out.println("exchange " + exchangeType.toString().toLowerCase() + " " + exchangeName);
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String answer = in.readLine();
            if (!answer.equals("ok")){
                return false;
            }
        } catch (IOException e){
            return false;
        }

        return true;
    }

    @Override
    public boolean queueBind(String queueName, String bindingKey) {
        out.println("queue " + queueName);
        try{
            String answer = in.readLine();
            if (!answer.equals("ok")){
                return false;
            }
            out.println("bind " + bindingKey);
            answer = in.readLine();
            if (!answer.equals("ok")){
                return false;
            }
        } catch (IOException e){
            return false;
        }

        return true;
    }

    @Override
    public Thread subscribe(Consumer<String> callback) {
        out.println("subscribe");

        try{
            String answer = in.readLine();
            if (!answer.equals("ok")){
                return null;
            }
        } catch (IOException e){
            return null;
        }

        Subscription subscription = new Subscription(this, callback);
        subscription.start();
        return subscription;
    }

    @Override
    public String getFromSubscription() {
        String msg = "";
        try {
            if (in.ready()) {
                msg = in.readLine();
            } else {
                return null;
            }
        } catch (IOException ignored) {
        }
        return msg;
    }

    @Override
    public boolean publish(String routingKey, String message) {
        out.println("publish " + routingKey + " " + message);

        try {
            String answer = in.readLine();
            if (answer == null || !answer.equals("ok")) {
                return false;
            }
        } catch (IOException e) {
            return false;
        }

        return true;
    }
}
