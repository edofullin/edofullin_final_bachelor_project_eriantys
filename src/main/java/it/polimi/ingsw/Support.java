package it.polimi.ingsw;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.ingsw.model.IStudentContainer;
import it.polimi.ingsw.model.SPColor;
import it.polimi.ingsw.model.characters.*;
import it.polimi.ingsw.network.messages.*;
import javafx.scene.control.Alert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * Support class
 */
public class Support {

    public final static int SERVER_CONTROL_PORT = 12166;
    public final static int SERVER_BROADCAST_PORT = 12165;
    public static Logger logger = LogManager.getLogger("mainlogger");

    private static Gson gson = null;

    /**
     * Returns the GSON instance
     *
     * @return the GSON instance
     */
    public static Gson GSON() {
        if (gson != null) return gson;

        RuntimeTypeAdapterFactory<Message> rtaf = RuntimeTypeAdapterFactory
                .of(Message.class)
                .registerSubtype(ChooseCharacterMessage.class)
                .registerSubtype(ChooseCloudMessage.class)
                .registerSubtype(ClientDisconnectMessage.class)
                .registerSubtype(DisconnectMessage.class)
                .registerSubtype(EvaluateMoveMessage.class)
                .registerSubtype(GameEndedMessage.class)
                .registerSubtype(GameStartedMessage.class)
                .registerSubtype(JoinRequestMessage.class)
                .registerSubtype(JoinResponseMessage.class)
                .registerSubtype(ModelUpdateMessage.class)
                .registerSubtype(MoveMotherNatureMessage.class)
                .registerSubtype(MoveStudentMessage.class)
                .registerSubtype(PingMessage.class)
                .registerSubtype(PlayCardMessage.class)
                .registerSubtype(PongMessage.class)
                .registerSubtype(TurnChangedMessage.class)
                .registerSubtype(CreateNewGameRequestMessage.class)
                .registerSubtype(CreateNewGameResponseMessage.class)
                .registerSubtype(ListGamesRequestMessage.class)
                .registerSubtype(ListGamesResponseMessage.class)
                .registerSubtype(RequestDisconnectMessage.class);

        RuntimeTypeAdapterFactory<CharacterCard> rtafcc = RuntimeTypeAdapterFactory.of(CharacterCard.class)
                .registerSubtype(CentaurCharacter.class)
                .registerSubtype(ChefCharacter.class)
                .registerSubtype(HerbalistCharacter.class)
                .registerSubtype(JokerCharacter.class)
                .registerSubtype(KnightCharacter.class)
                .registerSubtype(LadyCharacter.class)
                .registerSubtype(MerchantCharacter.class)
                .registerSubtype(MessengerCharacter.class)
                .registerSubtype(MusicianCharacter.class)
                .registerSubtype(PostmanCharacter.class)
                .registerSubtype(SinisterCharacter.class)
                .registerSubtype(SommelierCharacter.class);

        RuntimeTypeAdapterFactory<CharacterParametersBase> rtafccp = RuntimeTypeAdapterFactory.of(CharacterParametersBase.class)
                .registerSubtype(StudentsCharacterParameters.class)
                .registerSubtype(StudentIntCharacterParameters.class)
                .registerSubtype(IntCharacterParameters.class);

        gson = new GsonBuilder()
                .registerTypeAdapterFactory(rtaf)
                .registerTypeAdapterFactory(rtafcc)
                .registerTypeAdapterFactory(rtafccp)
                .create();

        return gson;
    }

    /**
     * Returns all broadcast addresses
     *
     * @return list of broadcast addresses
     */
    public static List<InetAddress> listAllBroadcastAddresses() throws SocketException {
        List<InetAddress> broadcastList = new ArrayList<>();
        Enumeration<NetworkInterface> interfaces
                = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();

            if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                continue;
            }

            networkInterface.getInterfaceAddresses().stream()
                    .map(a -> a.getBroadcast())
                    .filter(Objects::nonNull)
                    .forEach(broadcastList::add);
        }
        return broadcastList;
    }

    /**
     * Gets the Path used to save files relative this application
     *
     * @return
     */
    public static Path getAppDataPath() {
        if (System.getProperty("os.name").startsWith("Windows")) {
            return Paths.get(System.getenv("LOCALAPPDATA"), "Eriantys");
        } else if (System.getProperty("os.name").startsWith("Linux")) {
            return Paths.get(System.getProperty("user.home"), ".config", "Eriantys");
        } else if (System.getProperty("os.name").startsWith("Mac")) {
            return Paths.get(System.getProperty("user.home"), "Library", "Application Support", "Eriantys");
        }

        throw new RuntimeException("stai usando un os da disagiati");
    }

    /**
     * Retuns the positions of the maximum values
     *
     * @param array array to search in
     * @return positions of the maximum values
     */
    public static List<Integer> findMaxOrTiePosition(long[] array) {

        List<Integer> maxPos = new ArrayList<>();
        long maxPoints = -1;

        for (int i = 0; i < array.length; i++) {
            if (array[i] > maxPoints) {
                maxPos.clear();
                maxPos.add(i);
                maxPoints = array[i];
            } else if (array[i] == maxPoints) {
                maxPos.add(i);
            }
        }
        return maxPos;
    }

    /**
     * @param student     student to move
     * @param source      source of the student
     * @param destination destination of the student
     * @deprecated use add/remove
     * Muves a student from source to destination
     */
    public static void moveStudent(SPColor student, IStudentContainer source, IStudentContainer destination) {

        if (source != null)
            source.removeStudent(student);

        destination.addStudent(student);


    }

    /**
     * Checks the input is an integer
     *
     * @param selection input to check
     * @return true if the input is an integer
     */
    public static boolean controlsInput(@NotNull String selection) {

        if (selection.equals("")) return false;
        for (int i = 0; i < selection.length(); i++) {
            if (selection.charAt(i) < '0' || selection.charAt(i) > '9') return false;
        }
        return true;
    }

    /**
     * Used to ignore checked exceptions (debugging)
     *
     * @param callable callable to execute
     */
    public static void ignoreException(Checked callable) {
        try {
            callable.call();
        } catch (Exception e) {
            logger.debug("Trapped exception", e);
        }
    }

    /**
     * Used to ignore checked exceptions (debugging)
     *
     * @param callable     callable to execute
     * @param defaultValue default value to return if the callable throws an exception
     * @param <T>          type of the default value
     * @return the value returned by the callable or the default value if the callable throws an exception
     */
    public static <T> T ignoreException(Callable<T> callable, T defaultValue) {
        try {
            return callable.call();
        } catch (Exception e) {
            logger.debug("Trapped exception", e);
        }
        return defaultValue;
    }

    /**
     * Shows a JavaFX alert
     *
     * @param type    type of the alert
     * @param header  header of the alert
     * @param content content of the alert
     */
    public static void JFXAlertShowWait(Alert.AlertType type, String header, String content) {
        Alert alert = new Alert(type);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Return a simple hash of two values
     *
     * @param a first value
     * @param b second value
     * @return hash of the two values
     */
    public static int simpleHash(int a, int b) {
        String str = String.valueOf(b * 31) + a * 17;
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(str.getBytes());
            return new BigInteger(1, digest.digest()).intValue();
        } catch (NoSuchAlgorithmException e) {
            return str.hashCode();
        }
    }

    @FunctionalInterface
    public interface Checked {
        void call() throws Exception;
    }
}
