package it.polimi.ingsw.server;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.TowerColor;
import it.polimi.ingsw.network.messages.JoinRequestMessage;
import it.polimi.ingsw.network.messages.JoinResponseMessage;
import it.polimi.ingsw.network.messages.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * This class manages the connection with the clients.
 */
public class TCPConnectionManager implements IConnectionManager {

    private static final Logger logger = LogManager.getLogger(TCPConnectionManager.class);
    private final ArrayList<TCPClient> clients;
    private final int lobbySize;
    private final List<Player> expectedPlayers;
    private final List<ActionListener> actionListeners;
    private final int port;
    private boolean acceptingConnections = false;
    private ServerSocket serverSocket;

    /**
     * Creates a new connection manager
     *
     * @param port            the server port
     * @param lobbySize       the lobby size
     * @param expectedPlayers the list of player usernames we are expecting to connect (server persistance)
     */
    public TCPConnectionManager(int port, int lobbySize, List<Player> expectedPlayers) {
        this.clients = new ArrayList<>();
        this.port = port;
        this.lobbySize = lobbySize;
        this.actionListeners = new ArrayList<>();
        this.expectedPlayers = expectedPlayers;
    }

    public TCPConnectionManager(int port, int lobbySize) {
        this(port, lobbySize, null);
    }

    /**
     * Run on secondary threads from pool
     */
    private boolean handleNewConnection(Socket newPlayer) throws IOException {
        int retry = 0;
        boolean success = false;

        // newPlayer.setSoTimeout(2000); TODO mettere se non debug

        BufferedReader reader = new BufferedReader(new InputStreamReader(newPlayer.getInputStream()));
        PrintWriter writer = new PrintWriter(newPlayer.getOutputStream(), true);
        Message receivedMessage = null;
        JoinRequestMessage finalReqJoin = null;
        Player joiningPlayer = null;

        while (retry < 3) {
            retry++;

            try {
                receivedMessage = Message.fromJson(reader.readLine());
            } catch (SocketTimeoutException exc) {
                logger.warn("client %s did not setup in time".formatted(newPlayer.getRemoteSocketAddress().toString()));
                return false;
            }

            // il client ha chiuso la socket
            if (receivedMessage == null) {
                logger.warn("client %s closed the socket".formatted(newPlayer.getRemoteSocketAddress()));
                return false;
            }

            // unexpected message, respond fail and return
            if (receivedMessage.getClass() != JoinRequestMessage.class) {
                String respJson = new JoinResponseMessage(
                        receivedMessage.getMessageId(),
                        JoinResponseMessage.JoinReponseCode.JOIN_FAIL, -1)
                        .toJson();

                writer.println(respJson);
                logger.warn("unexpected message of type %s from client %s".formatted(receivedMessage.getClass().getName(), newPlayer.getRemoteSocketAddress().toString()));
                continue;
            }

            JoinRequestMessage reqJoin = (JoinRequestMessage) receivedMessage;

            if (clients.stream().anyMatch(cl -> Objects.equals(cl.getGamePlayer().getName(), reqJoin.getPlayerName()))) {
                String respJson = new JoinResponseMessage(
                        receivedMessage.getMessageId(),
                        JoinResponseMessage.JoinReponseCode.JOIN_FAIL_USERNAME_TAKEN, -1)
                        .toJson();
                logger.info("player from %s chose username %s already taken".formatted(newPlayer.getRemoteSocketAddress().toString(), reqJoin.getPlayerName()));
                writer.println(respJson);
                continue;
            }

            // if we load from disk we ignore mage decisions, they will be overridden when the model is loaded
            if (expectedPlayers == null && clients.stream().anyMatch(cl -> cl.getGamePlayer().getMagician() == reqJoin.getPlayerMagician())) {
                String respJson = new JoinResponseMessage(
                        receivedMessage.getMessageId(),
                        JoinResponseMessage.JoinReponseCode.JOIN_FAIL_MAGE_TAKEN, -1)
                        .toJson();
                logger.info("player from %s chose mage %s already taken".formatted(newPlayer.getRemoteSocketAddress().toString(), reqJoin.getPlayerMagician().toString()));
                writer.println(respJson);
                continue;
            }

            if (expectedPlayers != null) {

                if (expectedPlayers.stream().noneMatch(pl -> Objects.equals(pl.getName(), reqJoin.getPlayerName()))) {
                    String respJson = new JoinResponseMessage(
                            receivedMessage.getMessageId(),
                            JoinResponseMessage.JoinReponseCode.JOIN_FAIL_UNEXPECTED_USERNAME, -1)
                            .toJson();
                    logger.info("Player %s not expected".formatted(reqJoin.getPlayerName()));
                    writer.println(respJson);
                    continue;
                }

                joiningPlayer = expectedPlayers.stream().filter(pl -> Objects.equals(pl.getName(), reqJoin.getPlayerName())).findFirst().get();
                expectedPlayers.removeIf(p -> p.getName().equals(reqJoin.getPlayerName()));
            }

            success = true;
            finalReqJoin = reqJoin;
            break;
        }

        if (!success) {
            try {
                receivedMessage = Message.fromJson(reader.readLine());
            } catch (SocketTimeoutException exc) {
                logger.warn("Client %s did not setup in time".formatted(newPlayer.getRemoteSocketAddress().toString()));
                return false;
            }
            String respJson = new JoinResponseMessage(
                    receivedMessage.getMessageId(),
                    JoinResponseMessage.JoinReponseCode.JOIN_FAIL, -1)
                    .toJson();

            writer.println(respJson);
            logger.warn("Client %s dod not manage to setup, aborting".formatted(newPlayer.getRemoteSocketAddress()));
            return false;
        }

        newPlayer.setSoTimeout(0);

        // setup client and player
        TCPClient client;
        Player gamePlayer;

        if (expectedPlayers == null) {
            client = new TCPClient(clients.size(), clients.size() == 0);
            gamePlayer = new Player(finalReqJoin.getPlayerName(), clients.size(), TowerColor.values()[clients.size()], finalReqJoin.getPlayerMagician());
        } else {
            gamePlayer = joiningPlayer;
            client = new TCPClient(gamePlayer.getId(), gamePlayer.getId() == 0);
        }

        // client now connected
        client.setStatus(Client.ClientState.CONNECTED);
        client.setGamePlayer(gamePlayer);
        client.setSocket(newPlayer);

        client.registerMessageSubscriber((m, c) -> {
            disconnectAllClients();
            try {
                serverSocket.close();
            } catch (IOException e) {
                logger.error("Could not close server socket");
            }
        });

        clients.add(client);

        writer.println(
                new JoinResponseMessage(finalReqJoin.getMessageId(), JoinResponseMessage.JoinReponseCode.JOIN_OK, client.getId()).toJson()
        );

        // notify someone has connected
        notifyAction(
                new ActionEvent(this, 1, "client_connected")
        );

        new Thread(client::getMessages, String.format("player%d_messages", client.getId())).start(); // never terminates

        return true;
    }

    /**
     * Run on new Client Thread!
     */
    private void waitForNewClients() throws IOException, InterruptedException {
        serverSocket = new ServerSocket(this.port);

        ArrayList<CompletableFuture<Boolean>> handlers = new ArrayList<>();

        while (true) {

            if (!acceptingConnections)
                this.notifyAction(
                        new ActionEvent(this, 1, "server_ready")
                );

            acceptingConnections = true;

            Socket newPlayerSocket;

            try {
                newPlayerSocket = serverSocket.accept(); // accept new player socket
            } catch (SocketException exception) {
                logger.info("Socket closed in netman");
                halt();
                notifyAction(new ActionEvent(this, 1, "server_halt"));
                return;
            }

            logger.debug("accepted connection from %s".formatted(newPlayerSocket.getRemoteSocketAddress().toString()));

            handlers.add(CompletableFuture.supplyAsync(() -> {
                        try {
                            return handleNewConnection(newPlayerSocket);
                        } catch (IOException exc) {
                            return false;
                        }
                    }
            ).exceptionally((exc) -> false));

            // se si sono connessi N giocatori aspetto che il thread di connessione completi per tutti (o vada in timeout)
            if (handlers.size() == lobbySize) {
                int failIndex = -1;

                for (int i = 0; i < handlers.size(); i++) {
                    if (!handlers.get(i).join()) {
                        failIndex = i;
                        break;
                    }
                }

                if (failIndex == -1) {
                    break;
                } else {
                    handlers.remove(failIndex);
                }
            }
        }

        logger.debug("%d clients connected, waiting for setup to finish".formatted(clients.size()));

        notifyAction(
                new ActionEvent(this, 2, "lobby_full")
        );
    }

    public Client getClient(Player p) {
        return clients.stream().filter(c -> c.getGamePlayer().equals(p)).findFirst().get();
    }

    /**
     * Closes the connection manager
     */
    @Override
    public void halt() {
        disconnectAllClients();

        clients.clear();
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                logger.error("Could not close server socket");
            }
        }
    }

    /**
     * Waits for the specified number of clients to connect
     *
     * @throws InterruptedException interrupted
     */
    public void waitClients() throws InterruptedException {

        Thread waitThread = new Thread(() -> {
            try {
                waitForNewClients();
            } catch (IOException | InterruptedException e) {
                logger.error("cannot wait for clients");
                throw new RuntimeException(e);
            }
        });

        waitThread.setName("clientwait_thread");
        waitThread.start();

        waitThread.join();
    }

    /**
     * broadcasts a message
     *
     * @param m message
     */
    @Override
    public void broadcastMessage(Message m) {
        for (TCPClient client : clients) {
            sendMessageToClientAsync(client, m);
        }
    }

    /**
     * broadcasts a message  excluding a spceified client
     *
     * @param c excluded client
     * @param m message
     */
    @Override
    public void broadcastMessageExcept(Client c, Message m) {
        for (TCPClient client : clients) {
            if (!client.equals(c))
                sendMessageToClientAsync(client, m);
        }
    }

    /**
     * Registers the listener
     *
     * @param listener the listener
     */
    @Override
    public void registerActionListener(ActionListener listener) {
        actionListeners.add(listener);
    }

    public List<Client> getClients() {
        return new ArrayList<>(clients);
    }

    /**
     * Send a message to a specific client
     *
     * @param c the client
     * @param m
     */
    @Override
    public void sendMessageToClientAsync(Client c, Message m) {
        c.sendMessageAsync(m);
    }

    /**
     * Send a message to a specific client
     *
     * @param c the client
     * @param m
     */
    @Override
    public Message sendMessageToClient(Client c, Message m) throws InterruptedException {
        return c.sendMessage(m);

    }

    /**
     * Register a message subscriber for all clients
     *
     * @param subscriber the subscriber
     */
    public void registerMessageSubscriber(ClientMessageSubscriber subscriber) {
        for (Client c : clients) {
            c.registerMessageSubscriber(subscriber);
        }
    }

    private void notifyAction(ActionEvent event) {
        for (ActionListener actionListener : actionListeners) {
            actionListener.actionPerformed(event);
        }
    }

    /**
     * Disconnects all clients
     */
    public void disconnectAllClients() {
        logger.debug("Disconnecting all clients");

        clients.forEach(cl -> {
            if (cl.getStatus() == Client.ClientState.CONNECTED)
                cl.disconnect();
        });
    }


}

