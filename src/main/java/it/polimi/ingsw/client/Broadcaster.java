package it.polimi.ingsw.client;

import it.polimi.ingsw.Support;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.PingMessage;
import it.polimi.ingsw.network.messages.PongMessage;
import it.polimi.ingsw.server.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.*;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Broadcaster {

    final private static Logger logger = LogManager.getLogger(Broadcaster.class);
    private final Consumer<String> messageConsumer;
    private DatagramSocket broadcastSocket;
    private boolean running = false;
    private ScheduledExecutorService scheduler;

    public Broadcaster(Consumer<String> messageConsumer) {
        this.messageConsumer = messageConsumer;
    }

    /**
     * Starts a thread that sends udp messages to discover the server IP
     */
    public void start() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::run, 0, Configuration.getConfiguration().getBroadcasterInterval(), TimeUnit.SECONDS);

        logger.info("Starting broadcaster");
        running = true;
        Random random = new Random();
        try {
            broadcastSocket = new DatagramSocket(10000 + random.nextInt(10000), InetAddress.getByName("0.0.0.0"));
            broadcastSocket.setSoTimeout(1000);
            broadcastSocket.setBroadcast(true);
        } catch (IOException e) {
            logger.error("Could not run broadcast socket", e);
        }
    }

    /**
     * Stops broadcaster
     */
    public void stop() {
        if (!running) return;

        logger.warn("Stopping broadcaster");
        scheduler.shutdown();
        running = false;
        broadcastSocket.close();
    }

    /**
     * Runs the broadcast receiver (used to suggest the server IP to clients)
     */
    @SuppressWarnings("DuplicatedCode")
    private void run() {
        byte[] rcvbuffer = new byte[256];
        byte[] sndbuffer = new PingMessage().toJson().getBytes();

        DatagramPacket sndpacket = null;

        try {
            for (InetAddress address : Support.listAllBroadcastAddresses()) {
                sndpacket = new DatagramPacket(sndbuffer, sndbuffer.length, address, Support.SERVER_BROADCAST_PORT);

                logger.debug("Sending broadcast packet to %s".formatted(address));

                try {
                    broadcastSocket.send(sndpacket);
                } catch (IOException e) {
                    logger.warn("Could not send broadcast packet to " + sndpacket.getAddress().getHostAddress(), e);
                    return;
                }
            }
        } catch (SocketException e) {
            logger.error("could not get network interface list");
            stop();
            return;
        }

        DatagramPacket packet = new DatagramPacket(rcvbuffer, rcvbuffer.length);

        try {
            broadcastSocket.receive(packet);
        } catch (SocketTimeoutException exc) {
            logger.debug("Broadcast socket timeout, ignoring");
            return;
        } catch (IOException e) {
            logger.error("Could not receive packet", e);
            return;
        }

        Message message = Message.fromJson(new String(packet.getData(), 0, packet.getLength()));

        if (message instanceof PongMessage) {
            messageConsumer.accept(packet.getAddress().getHostAddress());
        }
    }

    /**
     * Returns true if the broadcaster is running
     *
     * @return true if the broadcaster is running
     */
    public boolean isRunning() {
        return running;
    }
}
