package it.polimi.ingsw.server;

import com.google.gson.stream.JsonReader;
import it.polimi.ingsw.Support;
import it.polimi.ingsw.network.GameSettings;
import it.polimi.ingsw.network.messages.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * This class handles the creation of logistics of creating a new game and the udp broadcaster
 * for suggesting the server ip address to the clients.
 */
public class NetworkGamesManager implements Runnable {

    private static final Logger logger = LogManager.getLogger(NetworkGamesManager.class);
    private final Thread broadcastHandlerThread;
    private final Thread gamesManagerThread;
    private final Server server;
    private final List<GameSettings> availableGamesFromDisk;
    private DatagramSocket broadcastSocket;
    private ServerSocket controlSocket;

    private Thread.UncaughtExceptionHandler exceptionHandler;

    public NetworkGamesManager(Server server) {
        this.server = server;
        this.availableGamesFromDisk = new ArrayList<>();

        broadcastHandlerThread = new Thread(this::runBroadcastReceiver, "ngm_broadcastreceiver");
        gamesManagerThread = new Thread(this::runGamesManager, "ngm_gamesmanager");
    }

    /**
     * Run the broadcast receiver (used to suggest the server IP to clients)
     */
    @SuppressWarnings("DuplicatedCode")
    private void runBroadcastReceiver() {
        logger.info("Starting broadcaster");
        byte[] rcvbuffer = new byte[256];

        try {
            broadcastSocket = new DatagramSocket(Support.SERVER_BROADCAST_PORT, InetAddress.getByName("0.0.0.0"));
            broadcastSocket.setBroadcast(true);
        } catch (IOException e) {
            logger.error("Could not run broadcast socket", e);
            return;
        }

        while (true) {

            DatagramPacket packet = new DatagramPacket(rcvbuffer, rcvbuffer.length);

            try {
                broadcastSocket.receive(packet);
            } catch (IOException e) {
                logger.error("Could not receive packet", e);
                break;
            }

            Message message = Message.fromJson(new String(packet.getData(), 0, packet.getLength()));

            logger.info("Received broadcast message of type %s from %s".formatted(message.getClass().getSimpleName(), packet.getAddress().getHostAddress()));

            if (message instanceof PingMessage) {
                byte[] sndbuffer = new PongMessage().toJson().getBytes();
                DatagramPacket sndpacket = new DatagramPacket(sndbuffer, sndbuffer.length, packet.getAddress(), packet.getPort());

                try {
                    broadcastSocket.send(sndpacket);
                } catch (IOException e) {
                    logger.warn("Could not send broadcast reply to " + packet.getAddress().getHostAddress(), e);
                    continue;
                }
            } else {
                logger.info("Unexpected message of type %s received from %s on broadcast socket".formatted(message.getClass().getSimpleName(), packet.getAddress().getHostAddress()));
            }
        }
    }


    private void updateUnfinishedGames() {
        File savesPath = Support.getAppDataPath().resolve("saves").toFile();

        logger.info("Loading unfinished games from disk");

        if (!savesPath.exists()) {
            savesPath.mkdirs();
            return;
        }

        for (File file : savesPath.listFiles()) {

            logger.info("Loading game from %s".formatted(file.getName()));
            try {
                JsonReader reader = new JsonReader(new FileReader(file));
                GameSave save = Message.GSON().fromJson(reader, GameSave.class);

                availableGamesFromDisk.add(new GameSettings(save.settings().getLobbyName(), save.settings().getLobbySize(), -1, save.settings().getExpertMode(), true, save.getSettings().getSaveOnDisk()));
                logger.info("Game %s loaded successfully".formatted(file.getName()));
            } catch (Exception exc) {
                logger.error("file %s cannot be read correctly".formatted(file.toString()), exc);
                continue;
            }
        }


    }

    private void runGamesManager() {

        logger.info("Starting lobbymanager");

        controlSocket = null;

        updateUnfinishedGames();

        try {
            //noinspection resource
            controlSocket = new ServerSocket(Support.SERVER_CONTROL_PORT);
        } catch (IOException e) {
            logger.error("Could not instantiate server socket");
            throw new RuntimeException(e);
        }


        List<CompletableFuture<Void>> futures = new ArrayList<>();

        while (!Thread.interrupted()) {


            Socket clientSocket = null;

            // sia maledetto chi ha inventato le eccezioni checked
            try {
                clientSocket = controlSocket.accept();
            } catch (IOException e) {
                logger.error("Could not handle client");
                continue;
            }

            Socket finalClientSocket = clientSocket;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {

                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(finalClientSocket.getInputStream()));
                    PrintWriter writer = new PrintWriter(finalClientSocket.getOutputStream(), true);

                    //clientSocket.setSoTimeout(1000);

                    while (true) {
                        String readData = reader.readLine();

                        if (readData == null) break;

                        Message message = Message.fromJson(readData);
                        Message response = handleMessage(message, false);

                        logger.debug("Message %s from client %s".formatted(message.getClass().getSimpleName(), finalClientSocket.getRemoteSocketAddress()));

                        if (response != null) {
                            writer.println(response.toJson());
                        }
                    }

                    logger.debug("Client %s terminated control connection".formatted(finalClientSocket.getRemoteSocketAddress()));

                    finalClientSocket.close();
                } catch (IOException exc) {
                    logger.error("error handling client %s".formatted(finalClientSocket.getRemoteSocketAddress()));
                }
            });

            futures.add(future);
        }


        for (CompletableFuture<Void> future : futures) {
            future.join();
        }
    }

    private Message handleMessage(Message message, boolean broadcastSource) {

        if (message instanceof CreateNewGameRequestMessage cng) {

            if (broadcastSource) return null;


            if (cng.getSettings().isFromDisk()) {
                try {
                    GameSave loadedSave = GameSave.load(cng.getSettings().getLobbyName());
                    loadedSave.getSettings().setPort(-1); // tell server to use first available port
                    loadedSave.getSettings().setFromDisk(true);

                    int port = server.startNewMatch(loadedSave, null);
                    loadedSave.getSettings().setPort(port); // tell client which port was chosen (might be redundant)

                    availableGamesFromDisk.removeIf(g -> Objects.equals(g.getLobbyName(), loadedSave.getSettings().getLobbyName()));
                    return new CreateNewGameResponseMessage(cng.getMessageId(), true, port);
                } catch (FileNotFoundException exception) {
                    logger.error(exception);
                    return new CreateNewGameResponseMessage(cng.getMessageId(), false, -1);
                }
            }

            if (cng.getSettings().getSaveOnDisk() && availableGamesFromDisk.stream().map(GameSettings::getLobbyName).toList().contains(cng.getSettings().getLobbyName())) {
                return new CreateNewGameResponseMessage(cng.getMessageId(), false, -1);
            }

            int port = server.startNewMatch(cng.getSettings(), null);

            return new CreateNewGameResponseMessage(cng.getMessageId(), true, port);

        }

        if (message instanceof ListGamesRequestMessage lgr) {
            List<GameSettings> availableGames = new ArrayList<>();
            availableGames.addAll(server.getAvailableGamesList());
            availableGames.addAll(availableGamesFromDisk);

            return new ListGamesResponseMessage(lgr.getMessageId(), availableGames);
        }

        return null;
    }


    public void setExceptionHandler(Thread.UncaughtExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    /**
     * Starts both the games manager and the broadcast receiver used to suggest clients the server IP
     */
    @Override
    public void run() {
        if (exceptionHandler != null) {
            gamesManagerThread.setUncaughtExceptionHandler(exceptionHandler);
        }

        gamesManagerThread.start();

        if (Configuration.getConfiguration().isServerBroadcasterEnabled())
            broadcastHandlerThread.start();

        try {
            gamesManagerThread.join();

            if (Configuration.getConfiguration().isServerBroadcasterEnabled())
                broadcastHandlerThread.join();
        } catch (InterruptedException e) {
            logger.info("lobby manager interrupted");
            return;
        }
    }

    /**
     * Waits for the lobby manager thread to complete
     */
    public void waitCompletion() {
        try {
            gamesManagerThread.join();
        } catch (InterruptedException e) {
            logger.info("lobby manager interrupted");
            return;
        }
    }
}
