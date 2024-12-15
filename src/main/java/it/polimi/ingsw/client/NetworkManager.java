package it.polimi.ingsw.client;

import it.polimi.ingsw.Support;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.PingMessage;
import it.polimi.ingsw.network.messages.PongMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class NetworkManager {

    final private static Logger logger = LogManager.getLogger(NetworkManager.class);
    private final String serverIP;
    private final int serverPort;
    private final Thread messagesThread;
    final private Executor notifyExecutor;
    private final AtomicLong waitMessageID = new AtomicLong();
    private final Consumer<Message> messageConsumer;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private Message waitingMessage;
    private Consumer<String> broadcastMessageConsumer;

    public NetworkManager(@NotNull String serverIP, int serverPort, Consumer<Message> messageConsumer) {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.messagesThread = new Thread(this::getMessages, "server_messages_thread");
        this.messageConsumer = messageConsumer;
        this.notifyExecutor = Executors.newSingleThreadExecutor();
    }

    public String getServerIP() {
        return serverIP;
    }

    /**
     * Accept all async messages
     */
    private void getMessages() {

        if (socket == null || reader == null) throw new RuntimeException("Socket nor started");

        while (true) {

            String readData = null;
            Message message;

            try {
                readData = reader.readLine();
            } catch (IOException exc) {
                logger.error("Could not get message from server");
            }

            if (readData == null) {
                Support.ignoreException(() -> socket.close());
                return;
            }


            try {
                message = Message.fromJson(readData);
            } catch (Exception exc) {
                logger.error("Could not get message from server");
                continue;
            }

            Message finalMessage = message;

            if (waitMessageID != null && waitMessageID.get() == finalMessage.getMessageId()) {
                waitingMessage = finalMessage;
                synchronized (waitMessageID) {
                    waitMessageID.notifyAll();
                }
                continue;
            }

            if (message instanceof PingMessage) {
                sendMessageAsync(new PongMessage(message.getMessageId()));
                continue;
            }

            if (messageConsumer != null) {
                notifyExecutor.execute(() -> messageConsumer.accept(finalMessage));
            }

        }

    }

    /**
     * Connects to the server
     *
     * @throws IOException during socket init
     */
    public void connect() throws IOException {
        if (socket != null) throw new RuntimeException("Socket already connected");

        SocketAddress address = new InetSocketAddress(serverIP, serverPort);
        socket = new Socket();
        socket.connect(address, 2000);

        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);

        this.messagesThread.start();
    }

    /**
     * Disconnects from the server
     */
    public void disconnect() {
        if (!isConnected()) return;


        try {
            socket.close();
        } catch (IOException e) {
            logger.warn("Could not close socket", e);
        }

        socket = null;
        reader = null;
        writer = null;
    }

    public boolean isConnected() {
        return socket != null;
    }

    /**
     * Send a synchronous message
     *
     * @param m       message
     * @param timeout timeout
     * @return answer
     */
    public Message sendMessage(@NotNull Message m, long timeout) {

        synchronized (waitMessageID) {
            waitMessageID.set(m.getMessageId());
            sendMessageAsync(m);

            try {
                waitMessageID.wait(timeout);
            } catch (InterruptedException exc) {
                logger.error(exc);
                return null;
            }
        }

        Message receivedMessage = waitingMessage;
        waitingMessage = null;

        return receivedMessage;
    }

    public Message sendMessage(@NotNull Message m) {
        return sendMessage(m, 0);
    }

    /**
     * Sends an async message
     *
     * @param m message
     */
    public void sendMessageAsync(Message m) {
        CompletableFuture
                .runAsync(() -> writer.println(m.toJson()))
                .exceptionally((e) -> {
                    logger.debug("could not send message", e);
                    throw new RuntimeException(e);
                });
    }
}
