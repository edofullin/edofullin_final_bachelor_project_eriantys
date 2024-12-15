package it.polimi.ingsw.server;

import com.google.gson.JsonSyntaxException;
import it.polimi.ingsw.network.messages.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.Instant;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This class handles a single client requests and forwards them to the game.
 */
public class TCPClient extends Client {

    private static final Logger logger = LogManager.getLogger(TCPClient.class);
    private final AtomicLong waitingID;
    private final Timer keepAliveTimer;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private Message waitingResponse;
    private long lastKeepAlive = 0L;

    public TCPClient(int id, boolean isGM) {
        super(id, isGM);
        this.waitingID = new AtomicLong();
        this.keepAliveTimer = new Timer();
    }

    public TCPClient(Client c) {
        super(c.getId(), c.isGM());
        this.socket = null;
        this.waitingID = new AtomicLong();
        this.keepAliveTimer = new Timer();
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) throws IOException {
        this.socket = socket;

        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.writer = new PrintWriter(socket.getOutputStream());
    }

    private void sendKeepAlive() {

        if (getStatus() != ClientState.CONNECTED) return;

        if (lastKeepAlive + Configuration.getConfiguration().getClientKeepAliveDead() < Instant.now().getEpochSecond()) {
            logger.info("Client %d is dead Jim, we need to shut down".formatted(getId()));

            // useless try catch
            try {
                socket.shutdownInput();
            } catch (IOException exc) {
                logger.debug("Could not close socket for client %d, ce ne faremo una ragione".formatted(getId()), exc);
            }

            keepAliveTimer.cancel();
            return;
        }

        try {
            logger.debug("Sending keepalive to client %d".formatted(getId()));
            sendMessageAsync(new PingMessage());
        } catch (Exception exc) {
            logger.error("Could not send keepalive to client %d".formatted(getId()));
            return;
        }
    }

    /**
     * Gets the current messages
     */
    @SuppressWarnings("IntegerMultiplicationImplicitCastToLong")
    public void getMessages() {

        final long kat = Configuration.getConfiguration().getClientKeepAliveInterval() * 1000;
        lastKeepAlive = Instant.now().getEpochSecond();

        if (Configuration.getConfiguration().getClientKeepAliveInterval() != 0)
            this.keepAliveTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    sendKeepAlive();
                }
            }, kat, kat);


        while (this.getStatus() == ClientState.CONNECTED) {


            String receivedData;

            try {
                receivedData = reader.readLine();
            } catch (IOException ioe) {
                logger.error("could not receive message from %d".formatted(getId()));
                receivedData = null;
            }

            if (receivedData == null) {
                logger.info("Client %d has disconnected, sending Disconnect message upstream".formatted(this.getId()));
                if (subscriber != null) subscriber.receivedClientMessageEvent(this, new DisconnectMessage());

                this.setStatus(ClientState.DISCONNECTED);
                break;
            }


            Message receivedMessage;

            try {
                receivedMessage = Message.fromJson(receivedData);
            } catch (JsonSyntaxException exc) {
                logger.error("message with content '%s' from client %d is is not well formatted".formatted(receivedData, this.getId()));
                continue;
            }

            logger.debug("received message of type %s from client %d".formatted(receivedMessage.getClass().getSimpleName(), this.getId()));

            if (waitingID.get() == receivedMessage.getMessageId()) {
                waitingResponse = receivedMessage;
                waitingID.notify();
                continue;
            }

            if (receivedMessage instanceof PongMessage) {
                this.lastKeepAlive = Instant.now().getEpochSecond();
                continue;
            }

            if (subscriber != null) subscriber.receivedClientMessageEvent(this, receivedMessage);

            if (receivedMessage instanceof DisconnectMessage) break;
        }

        this.keepAliveTimer.cancel();

        logger.debug("GetMessage for client %d has terminated".formatted(this.getId()));
        this.setStatus(ClientState.DISCONNECTED);
    }

    /**
     * Sends a message to the client asynchronously (not waiting for reply)
     *
     * @param m message to send
     */
    @Override
    public void sendMessageAsync(Message m) {
        logger.debug("sending message of type %s to client %d".formatted(m.getClass().getSimpleName(), super.getId()));
        writer.println(m.toJson());
        writer.flush();
    }

    /**
     * Sends a message to the client and waits for a reply
     *
     * @param m message to send
     * @return message received
     */
    @Override
    public Message sendMessage(Message m) throws InterruptedException {
        waitingID.set(m.getMessageId());
        writer.println(m.toJson());
        writer.flush();

        waitingID.wait();

        return waitingResponse;
    }

    /**
     * Disconnects this player and notifies the player that it's going to be disconnected
     *
     * @param reason disconnect reason
     */
    @Override
    public void disconnect(String reason) {
        if (getStatus() == ClientState.DISCONNECTED) return;
        this.setStatus(ClientState.DISCONNECTED);

        try {
            this.sendMessageAsync(new RequestDisconnectMessage(reason));
        } catch (Exception exc) {
            logger.warn("Could not gracefully disconnect client %d".formatted(getId()), exc);
        }

        try {
            this.socket.close();
        } catch (IOException exc) {
            logger.error("Cannot close socket for client %d".formatted(getId()));
        }
    }

    /**
     * Disconnects this player and notifies the player that it's going to be disconnected
     */
    @Override
    public void disconnect() {
        disconnect("");
    }
}
