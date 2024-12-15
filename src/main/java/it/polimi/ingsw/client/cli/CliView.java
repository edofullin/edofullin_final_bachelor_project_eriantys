package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.Support;
import it.polimi.ingsw.client.GameManager;
import it.polimi.ingsw.client.IView;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.characters.*;
import it.polimi.ingsw.network.GameSettings;
import it.polimi.ingsw.network.messages.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import static it.polimi.ingsw.Support.controlsInput;
import static it.polimi.ingsw.client.cli.CliApp.width;
import static it.polimi.ingsw.client.cli.EscapeCode.*;
import static java.lang.System.exit;

/**
 * This class is the view of the game. It is the CLI interface with the user.
 */
public class CliView implements IView {

    final private String PROMPT = ">> ";
    private GameManager gameManager;
    private String serverIP;
    private int serverPort;
    private String nickname;
    private Player.Magician magician;


    public CliView() {

    }

    /**
     * Print a line and return
     *
     * @param s the text to print
     */
    private void println(String s) {
        print(s);
        System.out.println();
    }

    /**
     * Print a line
     *
     * @param s the text to print
     */
    private void print(@NotNull String s) {
        int sLen = s.length();
        if (s.equals(PROMPT)) {
            System.out.print("\u001b[" + (width / 3) + "C");
            System.out.print(s);
            return;
        }
        if (sLen <= width) {
            if (width % 2 == 0) {
                if (sLen % 2 == 1) sLen++;
            } else {
                if (sLen % 2 == 0) sLen++;
            }
            System.out.print("\u001b[" + ((width - sLen) / 2) + "C");
            System.out.print(s);
        } else {
            if (sLen % 2 == 0) {
                println(s.substring(0, (sLen / 2) - 1));
                println(s.substring((sLen / 2) - 1));
            } else {
                println(s.substring(0, (sLen - 1) / 2));
                println(s.substring(((sLen + 1) / 2) - 1));
            }
        }
    }

    /**
     * Print a colorized text and return
     *
     * @param s the text to print
     */
    private void colorPrintln(String s) {
        colorPrint(s);
        System.out.println();
    }

    /**
     * Print a colorized text
     *
     * @param s the text to print
     */
    private void colorPrint(@NotNull String s) {
        int sLen = s.length();
        if (sLen <= width) {
            if (width % 2 == 0) {
                if (sLen % 2 == 1) sLen++;
            } else {
                if (sLen % 2 == 0) sLen++;
            }
            System.out.print("\u001b[" + (((width - sLen) / 2) + (WHITE_TEXT.toString().length() * color(s))) + "C");
            System.out.print(s);
        } else {
            if (sLen % 2 == 0) {
                colorPrintln(s.substring(0, (sLen / 2) - 1));
                colorPrintln(s.substring((sLen / 2) - 1));
            } else {
                colorPrintln(s.substring(0, (sLen - 1) / 2));
                colorPrintln(s.substring(((sLen + 1) / 2) - 1));
            }
        }
    }

    /**
     * Count how many color there are in a text divided by two
     *
     * @param s the text
     * @return the number of color divided by two
     */
    private int color(@NotNull String s) {
        int res = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '[') res++;
        }
        return res / 2;
    }

    /**
     * Check if a text is a boolean
     *
     * @param exp the text
     * @return a boolean
     */
    private boolean checkBoolean(@NotNull String exp) {
        ArrayList<String> tf = new ArrayList<>();
        tf.add("true");
        tf.add("false");
        return !tf.contains(exp);
    }

    /**
     * Find the color's escape code
     *
     * @param color the color
     * @return the escape code
     */
    private EscapeCode getColor(@NotNull SPColor color) {
        switch (color) {
            case RED -> {
                return RED_TEXT;
            }
            case BLUE -> {
                return BLUE_TEXT;
            }
            case PINK -> {
                return PINK_TEXT;
            }
            case GREEN -> {
                return GREEN_TEXT;
            }
            case YELLOW -> {
                return YELLOW_TEXT;
            }
        }
        return WHITE_TEXT;
    }

    /**
     * First method called
     */
    @Override
    public void start() throws IOException {

        Scanner input = new Scanner(System.in);

        print(CURSOR_TO_START.toString() + CLEAR_TERMINAL);

        if (width > 150) {
            colorPrintln(BLUE_TEXT + "█   █ █████ █      ███   ███  █   █ █████   █████  ███    █████ ████  █   █  ███  █   █ █████ █ █████" + WHITE_TEXT);
            colorPrintln(BLUE_TEXT + "█   █ █     █     █   █ █   █ ██ ██ █         █   █   █   █     █   █  █ █  █   █ ██  █   █   █ █    " + WHITE_TEXT);
            colorPrintln(BLUE_TEXT + "█   █ █████ █     █     █   █ █ █ █ █████     █   █   █   █████ ████    █   █████ █ █ █   █   █ █████" + WHITE_TEXT);
            colorPrintln(BLUE_TEXT + "█ █ █ █     █     █   █ █   █ █   █ █         █   █   █   █     █   █  █    █   █ █  ██   █   █     █" + WHITE_TEXT);
            colorPrintln(BLUE_TEXT + " █ █  █████ █████  ███   ███  █   █ █████     █    ███    █████ █   █ █     █   █ █   █   █   █ █████" + WHITE_TEXT);
        } else {
            colorPrintln(CYAN_TEXT + "Welcome to Eriantys!" + WHITE_TEXT);
        }

        println(EMPTY_LINE.toString());
        println("Enter to start");
        input.nextLine();

        println(CURSOR_TO_START.toString() + CLEAR_TERMINAL);

        colorPrintln(CYAN_TEXT + "INSERT SERVER IP: " + WHITE_TEXT);
        print(PROMPT);
        serverIP = input.nextLine();

        println(EMPTY_LINE.toString());

        try {
            selectLobby();
            connect();
        } catch (IOException ignored) {
        } catch (InterruptedException e) {
            CliApp.start();
            throw new IOException(e);
        }

    }

    /**
     * Lobby selection view
     *
     * @throws IOException during socket initialization
     */
    private void selectLobby() throws IOException {

        Scanner input = new Scanner(System.in);
        Socket connList;

        try {
            checkIP(serverIP);
            connList = new Socket(serverIP, Support.SERVER_CONTROL_PORT);
        } catch (Exception ex) {
            CliApp.start();
            throw new IOException(ex);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(connList.getInputStream()));
        PrintWriter writer = new PrintWriter(connList.getOutputStream(), true);

        writer.println(new ListGamesRequestMessage().toJson());

        String respStr = reader.readLine();

        Message response = Message.fromJson(respStr);

        if (!(response instanceof ListGamesResponseMessage)) {
            exit(-1);
        }

        List<GameSettings> games = ((ListGamesResponseMessage) response).getGames();

        int selected = displayLobbies(input, games);

        if (selected == 0) {
            int port = createNewGame(writer, reader);
            connList.close();
            serverPort = port;
            return;
        }

        if (games.get(selected - 1).isFromDisk()) {
            CreateNewGameRequestMessage ms = new CreateNewGameRequestMessage(games.get(selected - 1));
            writer.println(ms.toJson());
            writer.flush();

            String readData = reader.readLine();

            Message message = Message.fromJson(readData);
            if (!(message instanceof CreateNewGameResponseMessage)) {
                writer.close();
                exit(-1);
                return;
            }

            serverPort = ((CreateNewGameResponseMessage) message).getPortNumber();
            connList.close();
            return;
        }


        connList.close();
        serverPort = games.get(selected - 1).getPort();
    }

    /**
     * Check if a string is an IP
     *
     * @param serverIP the string
     * @throws UnknownHostException if it is not an IP
     */
    private void checkIP(@NotNull String serverIP) throws UnknownHostException {
        int points = 0;
        if (serverIP.equals("")) return;
        for (int i = 0; i < serverIP.length(); i++) {
            if (serverIP.charAt(i) == '.') points++;
            else if (serverIP.charAt(i) < '0' && serverIP.charAt(i) > '9') throw new UnknownHostException();
        }
        if (points != 3) throw new UnknownHostException();
    }

    /**
     * Display lobbies
     *
     * @param input scanner
     * @param games game's list
     * @return selected lobby
     */
    private int displayLobbies(Scanner input, @NotNull List<GameSettings> games) {

        String selection;
        int selected = -1;

        do {
            colorPrintln(CYAN_TEXT + "PLEASE SELECT THE ID OF THE GAME YOU WANT TO CONNECT TO:" + WHITE_TEXT);
            println(EMPTY_LINE.toString());

            colorPrintln(CYAN_TEXT + "Enter 0 to create and join new game" + WHITE_TEXT);

            println(EMPTY_LINE.toString());
            for (int i = 0; i < games.size(); i++) {
                String txt = " on port " + games.get(i).getPort();
                if (games.get(i).getPort() == -1)
                    txt = "";
                println("Enter " + (i + 1) + " to join game " + games.get(i).getLobbyName() + txt + ", size: " + games.get(i).getLobbySize() + " " + (games.get(i).getExpertMode() ? "(expert mode)" : ""));
            }
            println(EMPTY_LINE.toString());

            print(PROMPT);
            System.out.flush();
            selection = input.nextLine();
            if (controlsInput(selection)) selected = Integer.parseInt(selection);
            println(EMPTY_LINE.toString());
            if (selected < 0 || selected > games.size()) {
                colorPrintln(RED_TEXT + "NO GAME WITH THAT ID" + WHITE_TEXT);
                println(EMPTY_LINE.toString());
                println("Enter to retry");
                input.nextLine();
                println(CURSOR_TO_START.toString() + CLEAR_TERMINAL);
            }
        } while (selected < 0 || selected > games.size());

        return selected;
    }

    /**
     * New game view
     *
     * @param writer writer
     * @param reader reader
     * @return lobby's ID
     * @throws IOException while waiting player's input
     */
    private int createNewGame(@NotNull PrintWriter writer, @NotNull BufferedReader reader) throws IOException {

        Scanner in = new Scanner(System.in);

        String lobbyName = lobbyName(in);

        boolean expert = exp(in);

        int size = gameSize(in);

        boolean sod = saveOnDisk(in);

        CreateNewGameRequestMessage ms = new CreateNewGameRequestMessage(new GameSettings(lobbyName, size, -1, expert, sod));
        writer.println(ms.toJson());
        writer.flush();

        String readData = reader.readLine();

        Message message = Message.fromJson(readData);
        if (!(message instanceof CreateNewGameResponseMessage)) {
            writer.close();
            exit(-1);
            return -1;
        }

        return ((CreateNewGameResponseMessage) message).getPortNumber();
    }

    /**
     * Request if the game will be saved
     *
     * @param in scanner
     * @return if the game will be saved
     */
    private boolean saveOnDisk(@NotNull Scanner in) {
        String input;

        do {
            colorPrintln(CYAN_TEXT + "DO YOU WANT TO SAVE THE GAME ON DISK (T/F): " + WHITE_TEXT);
            input = askBoolean(in);
        } while (checkBoolean(input));

        return Boolean.parseBoolean(input);
    }

    /**
     * Asks for a boolean
     *
     * @param in scanner
     * @return a string representing a boolean
     */
    @NotNull
    private String askBoolean(@NotNull Scanner in) {
        String input;
        print(PROMPT);
        input = in.nextLine();
        println(EMPTY_LINE.toString());
        input = input.toUpperCase();
        input = completeText(input);
        input = input.toLowerCase();
        if (checkBoolean(input)) {
            colorPrintln(RED_TEXT + "YOU HAVE TO CHOOSE TRUE OR FALSE" + WHITE_TEXT);
            println(EMPTY_LINE.toString());
            println("Enter to retry");
            in.nextLine();
            println(CURSOR_TO_LAST_ACTION.toString() + CLEAR_TERMINAL);
        }
        return input;
    }

    /**
     * Select name for the new game
     *
     * @param in scanner
     * @return the name
     */
    private String lobbyName(@NotNull Scanner in) {
        String lobbyName;

        do {
            colorPrintln(CYAN_TEXT + "CHOOSE LOBBY'S NAME: " + WHITE_TEXT);
            print(PROMPT);
            lobbyName = in.nextLine();
            println(EMPTY_LINE.toString());
            if (lobbyName.equals("")) {
                colorPrintln(RED_TEXT + "LOBBY'S NAME CANNOT BE EMPTY" + WHITE_TEXT);
                println(EMPTY_LINE.toString());
                println("Enter to retry");
                in.nextLine();
                println(CURSOR_TO_LAST_ACTION.toString() + CLEAR_TERMINAL);
            }
        } while (lobbyName.equals(""));

        return lobbyName;
    }

    /**
     * Select mode for the new game
     *
     * @param in scanner
     * @return the mode
     */
    private @NotNull Boolean exp(@NotNull Scanner in) {
        String exp;

        do {
            colorPrintln(CYAN_TEXT + "CHOOSE EXPERT MODE(True/False): " + WHITE_TEXT);
            exp = askBoolean(in);
        } while (checkBoolean(exp));

        return Boolean.parseBoolean(exp);
    }

    /**
     * Select the size for the new game
     *
     * @param in scanner
     * @return the size
     */
    private int gameSize(@NotNull Scanner in) {
        String gameSize;
        int size = 1;

        do {
            colorPrintln(CYAN_TEXT + "CHOOSE GAME'S SIZE(2 or 3): " + WHITE_TEXT);
            print(PROMPT);
            gameSize = in.nextLine();
            if (controlsInput(gameSize)) size = Integer.parseInt(gameSize);
            println(EMPTY_LINE.toString());
            if (size < 2 || size > 3) {
                colorPrintln(RED_TEXT + "YOU CANNOT CREATE A GAME WITH THAT SIZE" + WHITE_TEXT);
                println(EMPTY_LINE.toString());
                println("Enter to retry");
                in.nextLine();
                println(CURSOR_TO_LAST_ACTION.toString() + CLEAR_TERMINAL);
            }
        } while (size < 2 || size > 3);

        return size;
    }

    /**
     * Create and connect to the game manager
     *
     * @throws IOException while starting the game manager
     */
    private void connect() throws IOException, InterruptedException {
        Thread.sleep(500);
        gameManager = new GameManager(serverIP, serverPort);
        gameManager.registerView(this);

        try {
            gameManager.start();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }

    /**
     * Selection player's attributes view
     *
     * @param respCode the reason why the join failed
     */
    @Override
    public void beginRequestLoginInfo(JoinResponseMessage.JoinReponseCode respCode) {

        Scanner input = new Scanner(System.in);

        try {
            loginClear(respCode);
            username(input);
            this.magician = mage(input);
            gameManager.endRequestLoginInfo(nickname, this.magician);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Clear all screen after join fail
     *
     * @param respCode why join failed
     * @throws IOException waiting player's input
     */
    private void loginClear(JoinResponseMessage.JoinReponseCode respCode) throws IOException {
        if (respCode == null) return;

        Scanner in = new Scanner(System.in);
        println(EMPTY_LINE.toString());
        switch (respCode) {
            case JOIN_FAIL_USERNAME_TAKEN -> colorPrintln(RED_TEXT + "USERNAME ALREADY TAKEN" + WHITE_TEXT);
            case JOIN_FAIL_MAGE_TAKEN -> colorPrintln(RED_TEXT + "MAGE ALREADY TAKEN" + WHITE_TEXT);
        }
        println(EMPTY_LINE.toString());
        println("Enter to retry");
        in.nextLine();
        println(EMPTY_LINE.toString());
        println(EMPTY_LINE.toString());
        println(EMPTY_LINE.toString());
        println(CURSOR_TO_LAST_ACTION.toString() + CLEAR_TERMINAL);
        println(CURSOR_TO_LAST_ACTION.toString() + CLEAR_TERMINAL);
    }

    /**
     * Username's selection view
     *
     * @param input scanner
     * @throws IOException waiting player's input
     */
    private void username(@NotNull Scanner input) throws IOException {
        Scanner in = new Scanner(System.in);
        do {
            colorPrintln(CYAN_TEXT + "CHOOSE YOUR USERNAME: " + WHITE_TEXT);
            print(PROMPT);
            nickname = input.nextLine();
            println(EMPTY_LINE.toString());
            if (nickname.equals("")) {
                colorPrintln(RED_TEXT + "USERNAME CANNOT BE EMPTY" + WHITE_TEXT);
                println(EMPTY_LINE.toString());
                println("Enter to retry");
                in.nextLine();
                println(CURSOR_TO_LAST_ACTION.toString() + CLEAR_TERMINAL);
            }
        } while (nickname.equals("") || nickname.length() > 15);
    }

    /**
     * Mage's selection view
     *
     * @param input scanner
     * @return selected mage
     * @throws IOException waiting player's input
     */
    private Player.Magician mage(@NotNull Scanner input) throws IOException {
        String magician;

        do {
            colorPrintln(CYAN_TEXT + "CHOOSE YOUR MAGE (Odin, Xerxes, Rias, Marin): " + WHITE_TEXT);
            print(PROMPT);
            magician = input.nextLine();
            println(EMPTY_LINE.toString());
            magician = magician.toUpperCase();
            if (!magician.equals("ODIN") && !magician.equals("XERXES") && !magician.equals("RIAS") && !magician.equals("MARIN")) {
                if (magician.equals("")) magician = "'null'";
                colorPrintln(RED_TEXT + "MAGE " + magician + " DOES NOT EXISTS" + WHITE_TEXT);
                println(EMPTY_LINE.toString());
                println("Enter to retry");
                input.nextLine();
                println(CURSOR_TO_LAST_ACTION.toString() + CLEAR_TERMINAL);
            }
        } while (!magician.equals("ODIN") && !magician.equals("XERXES") && !magician.equals("RIAS") && !magician.equals("MARIN"));

        return Player.Magician.valueOf(magician);
    }

    /**
     * Waiting start's view
     */
    @Override
    public void notifyLoginOk() {
        println(CURSOR_TO_START.toString() + CLEAR_TERMINAL);
        colorPrintln(CYAN_TEXT + "YOU ARE IN, WAITING FOR OTHER PLAYERS..." + WHITE_TEXT);
    }

    /**
     * Exit game's view
     */
    @Override
    public void exitGame(String message) {
        Scanner in = new Scanner(System.in);
        println(EMPTY_LINE.toString());
        colorPrintln(RED_TEXT + message + WHITE_TEXT);
        println(EMPTY_LINE.toString());
        colorPrintln(RED_TEXT + "Exiting game..." + WHITE_TEXT);
        println(EMPTY_LINE.toString());
        println("Enter to exit");
        in.nextLine();
        System.exit(0);
    }

    /**
     * Useful only in GUI
     */
    @Override
    public void notifyGameStarted() {

    }

    /**
     * Assistant's request
     *
     * @param b boolean first try / previous error
     */
    @Override
    public void beginRequestAssistantCard(boolean b) {

        Scanner input = new Scanner(System.in);

        try {
            if (b) printErrorAssistant();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }


        println(CURSOR_TO_START.toString() + CLEAR_TERMINAL);

        printHand();

        int myGraveSize = gameManager.getModel().getGameBoard().getGraveyard(gameManager.getMyPlayer().getId()).getCards().size();
        ArrayList<CardStack> grave = new ArrayList<>();

        for (int i = 0; i < gameManager.getModel().getPlayers().size(); i++) {
            if (i != gameManager.getMyPlayer().getId()) {
                if (gameManager.getModel().getGameBoard().getGraveyard(i).getCards().size() > myGraveSize) {
                    grave.add(gameManager.getModel().getGameBoard().getGraveyard(i));
                }
            }
        }

        printCardsPlayed(grave);

        int card;
        try {
            card = selectCard(input);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }


        gameManager.endRequestAssistCard(gameManager.getMyPlayer().getHand().get(card));
    }

    /**
     * Assistant error's view
     *
     * @throws IOException waiting player's input
     */
    private void printErrorAssistant() throws IOException {
        Scanner in = new Scanner(System.in);
        println(EMPTY_LINE.toString());
        colorPrintln(RED_TEXT + "YOU CANNOT PLAY AN ASSISTANT WITH THAT VALUE" + WHITE_TEXT);
        println(EMPTY_LINE.toString());
        println("Enter to retry");
        in.nextLine();
    }

    /**
     * Hand's view
     */
    private void printHand() {
        List<AssistCard> hand = gameManager.getMyPlayer().getHand();

        colorPrintln(CYAN_TEXT + "YOUR HAND:" + WHITE_TEXT);
        println(EMPTY_LINE.toString());

        for (int i = 0; i < hand.size(); i++) {
            println("Card " + i +
                    ": " + hand.get(i).getAnimal() +
                    ", power: " + hand.get(i).getPower() +
                    ", moves: " + hand.get(i).getMoves());
        }
        println(EMPTY_LINE.toString());
    }

    /**
     * Opponents' cards
     *
     * @param grave opponents' cards
     */
    private void printCardsPlayed(@NotNull ArrayList<CardStack> grave) {
        if (grave.size() > 0) {
            colorPrintln(CYAN_TEXT + "CARDS PLAYED: " + WHITE_TEXT);
            println(EMPTY_LINE.toString());

            for (CardStack cardStack : grave) {
                AssistCard assistCard = cardStack.getTopCard();
                println(cardStack.getOwner().getName() +
                        " played card: " + assistCard.getAnimal() +
                        ", power: " + assistCard.getPower() +
                        ", moves: " + assistCard.getMoves());
            }
            println(EMPTY_LINE.toString());
        }
    }

    /**
     * Assistant's view
     *
     * @param input scanner
     * @return selected card
     * @throws IOException waiting player's input
     */
    private int selectCard(@NotNull Scanner input) throws IOException {
        String in;
        int card = -1;

        do {
            colorPrintln(CYAN_TEXT + "CHOOSE AN ASSISTANT CARD TO PLAY (0 to " + (gameManager.getMyPlayer().getHand().size() - 1) + "): " + WHITE_TEXT);
            print(PROMPT);
            in = input.nextLine();
            if (controlsInput(in)) card = Integer.parseInt(in);
            println(EMPTY_LINE.toString());
            if (!controlsInput(in) || card < 0 || card > gameManager.getMyPlayer().getHand().size() - 1) {
                colorPrintln(RED_TEXT + "THERE IS NO ASSISTANT WITH THAT VALUE" + WHITE_TEXT);
                println(EMPTY_LINE.toString());
                println("Enter to retry");
                input.nextLine();
                println(EMPTY_LINE.toString());
                println(CURSOR_TO_LAST_ACTION.toString() + CLEAR_TERMINAL);
            }
        } while (card < 0 || card > gameManager.getMyPlayer().getHand().size() - 1);

        return card;
    }

    /**
     * Character's request
     *
     * @param b first try / previous error
     */
    @Override
    public void beginRequestCharacter(String b) {
        Scanner input = new Scanner(System.in);
        if (b != null) {
            println(EMPTY_LINE.toString());
            colorPrintln(RED_TEXT + "MOVE NOT OK. Reason: " + b + WHITE_TEXT);
            println(EMPTY_LINE.toString());
            println("Enter to retry");
            input.nextLine();

        }

        println(CURSOR_TO_START.toString() + CLEAR_TERMINAL);

        String in;
        int in2;
        int character = -1;

        do {
            colorPrintln(CYAN_TEXT + "DO YOU WANT TO ACTIVATE A CHARACTER?" + WHITE_TEXT);
            println(EMPTY_LINE.toString());
            infoCharacters();
            println(" 1 - Your entrance                 ");
            println(" 2 - Your canteen                  ");
            println(" 3 - Opponents' canteens           ");
            println(" 4 - Islands                       ");
            println("10 - Info character 0              ");
            println("11 - Info character 1              ");
            println("12 - Info character 2              ");
            println(EMPTY_LINE.toString());
            colorPrintln(CYAN_TEXT + "YES / NO / 1-4 / 10-12" + WHITE_TEXT);
            print(PROMPT);
            in = input.nextLine();
            println(EMPTY_LINE.toString());
            in2 = 0;
            if (controlsInput(in)) in2 = Integer.parseInt(in);
            if (!in.equalsIgnoreCase("yes") && !in.equalsIgnoreCase("no") && !((in2 > 0 && in2 < 5) || (in2 > 9 && in2 < 13)))
                error();
            println(CURSOR_TO_START.toString() + CLEAR_TERMINAL);
            selectMenu(in2);
            if ((in2 > 0 && in2 < 5) || (in2 > 9 && in2 < 13)) {
                println("Enter to continue playing");
                input.nextLine();
            }
            println(CURSOR_TO_START.toString() + CLEAR_TERMINAL);
        } while (!in.equalsIgnoreCase("yes") && !in.equalsIgnoreCase("no"));

        if (in.equalsIgnoreCase("no")) {
            gameManager.endRequestCharacter(null, null);
            return;
        }

        do {
            infoCharacters();
            colorPrintln(CYAN_TEXT + "CHOOSE A CHARACTER: " + WHITE_TEXT);
            print(PROMPT);
            in = input.nextLine();
            if (controlsInput(in)) character = Integer.parseInt(in);
            println(EMPTY_LINE.toString());
            if (character < 0 || character > 2) error();
        } while (character < 0 || character > 2);

        CharacterCard characterCard = gameManager.getModel().getGameBoard().getCharacterCards().get(character);
        CharacterParametersBase param = null;

        println(CURSOR_TO_START.toString() + CLEAR_TERMINAL);
        colorPrintln(CYAN_TEXT + "YOU SELECTED THE " + characterCard.getEnumType() + WHITE_TEXT);
        println(EMPTY_LINE.toString());

        switch (characterCard.getEnumType()) {
            case SOMMELIER:
                param = sommelier(input, characterCard);
                break;
            case HERBALIST:
            case MESSENGER:
                param = herbalistMessenger(input);
                break;
            case JOKER:
                param = joker(input, characterCard);
                break;
            case MERCHANT:
            case SINISTER:
                param = merchantSinister(input);
                break;
            case MUSICIAN:
                param = musician(input);
                break;
            case LADY:
                param = lady(input, characterCard);
                break;
            case CHEF:
            case POSTMAN:
            case CENTAUR:
            case KNIGHT:
                break;

        }

        gameManager.endRequestCharacter(characterCard.getEnumType(), param);
    }

    /**
     * Active characters and coins
     */
    private void infoCharacters() {
        for (int i = 0; i < gameManager.getModel().getGameBoard().getCharacterCards().size(); i++)
            println("Character " + i + " : " + gameManager.getModel().getGameBoard().getCharacterCards().get(i).getEnumType() +
                    " . Cost: " + gameManager.getModel().getGameBoard().getCharacterCards().get(i).getCost());
        println(EMPTY_LINE.toString());
        colorPrintln(CYAN_TEXT + "You have " + gameManager.getModel().getPlayers().get(gameManager.getMyPlayer().getId()).getCoins() + " coins" + WHITE_TEXT);
        println(EMPTY_LINE.toString());
    }

    /**
     * Main switch in menù selection
     *
     * @param i selection
     */
    private void selectMenu(int i) {
        CharacterCard c0 = null;
        CharacterCard c1 = null;
        CharacterCard c2 = null;
        CharacterEnum e0 = null;
        CharacterEnum e1 = null;
        CharacterEnum e2 = null;
        if (gameManager.getModel().getMode()) {
            c0 = gameManager.getModel().getGameBoard().getCharacterCards().get(0);
            c1 = gameManager.getModel().getGameBoard().getCharacterCards().get(1);
            c2 = gameManager.getModel().getGameBoard().getCharacterCards().get(2);
            e0 = c0.getEnumType();
            e1 = c1.getEnumType();
            e2 = c2.getEnumType();
        }

        switch (i) {
            case 1 -> printEntrance();
            case 2 -> {
                colorPrintln(CYAN_TEXT + "YOUR CANTEEN:" + WHITE_TEXT);
                println(EMPTY_LINE.toString());
                for (SPColor color : SPColor.values()) {
                    colorPrintln("You have " + getColor(color).toString() +
                            gameManager.getMyPlayer().getBoard().getCanteen().getStudents().stream().filter(s -> s == color).count() + " " +
                            color.toString().toLowerCase() + WHITE_TEXT + " students");
                }
                println(EMPTY_LINE.toString());
                for (SPColor color : SPColor.values()) {
                    if (gameManager.getMyPlayer().getBoard().getCanteen().getProfessors().contains(color)) {
                        colorPrintln("You own the " + getColor(color).toString() + color.toString().toLowerCase() + WHITE_TEXT + " professor");
                    }
                }
                println(EMPTY_LINE.toString());
                colorPrintln(CYAN_TEXT + "You have " + gameManager.getMyPlayer().getBoard().getTowers().size() + " towers left" + WHITE_TEXT);
                println(EMPTY_LINE.toString());
            }
            case 3 -> {
                colorPrintln(CYAN_TEXT + "OPPONENTS' CANTEENS:" + WHITE_TEXT);
                println(EMPTY_LINE.toString());
                for (Player player : gameManager.getModel().getPlayers()) {
                    if (player.getId() != gameManager.getMyPlayer().getId()) {
                        for (SPColor color : SPColor.values()) {
                            colorPrintln(player.getName() +
                                    getColor(color).toString() + " has " +
                                    gameManager.getModel().getGameBoard().getPlayerBoard(player.getId()).getCanteen().getStudents().stream().filter(s -> s == color).count() + " " +
                                    color.toString().toLowerCase() + WHITE_TEXT + " students");
                        }
                        println(EMPTY_LINE.toString());
                        for (SPColor color : SPColor.values()) {
                            if (gameManager.getModel().getGameBoard().getPlayerBoard(player.getId()).getCanteen().getProfessors().contains(color)) {
                                colorPrintln(player.getName() + " owns the " +
                                        getColor(color).toString() + color.toString().toLowerCase() + WHITE_TEXT + " professor");
                            }
                        }
                        println(EMPTY_LINE.toString());
                        colorPrintln(CYAN_TEXT + player.getName() + " has " + gameManager.getModel().getGameBoard().getPlayerBoard(player.getId()).getTowers().size() + " towers left" + WHITE_TEXT);
                        println(EMPTY_LINE.toString());
                    }
                }
                println(EMPTY_LINE.toString());
            }
            case 4 -> {
                printIslands();
                println(EMPTY_LINE.toString());
            }
            case 10 -> {
                assert e0 != null;
                charactersAttributes(c0, e0);
            }
            case 11 -> {
                assert e1 != null;
                charactersAttributes(c1, e1);
            }
            case 12 -> {
                assert e2 != null;
                charactersAttributes(c2, e2);
            }
        }
    }

    /**
     * Characters' attributes
     *
     * @param c characterCard
     * @param e characterEnum
     */
    private void charactersAttributes(CharacterCard c, @NotNull CharacterEnum e) {
        switch (e) {
            case HERBALIST -> {
                colorPrintln(CYAN_TEXT + "HERBALIST" + WHITE_TEXT);
                println(EMPTY_LINE.toString());
                println("Bans: " + ((HerbalistCharacter) c).getBans());
            }
            case SOMMELIER -> {
                colorPrintln(CYAN_TEXT + "SOMMELIER" + WHITE_TEXT);
                println(EMPTY_LINE.toString());
                for (SPColor student : ((SommelierCharacter) c).getStudents()) {
                    colorPrintln(getColor(student) + "" + student + WHITE_TEXT);
                }
            }
            case JOKER -> {
                colorPrintln(CYAN_TEXT + "JOKER" + WHITE_TEXT);
                println(EMPTY_LINE.toString());
                for (SPColor student : ((JokerCharacter) c).getStudents()) {
                    colorPrintln(getColor(student) + "" + student + WHITE_TEXT);
                }
            }
            case LADY -> {
                colorPrintln(CYAN_TEXT + "LADY" + WHITE_TEXT);
                println(EMPTY_LINE.toString());
                for (SPColor student : ((LadyCharacter) c).getStudents()) {
                    colorPrintln(getColor(student) + "" + student + WHITE_TEXT);
                }
            }
        }
        println(EMPTY_LINE.toString());
        println(e.getDescription());
        println(EMPTY_LINE.toString());
    }

    /**
     * Get a color
     *
     * @param input scanner
     * @return a color
     */
    @NotNull
    private String getString(@NotNull Scanner input) {
        String student;
        print(PROMPT);
        student = input.nextLine().toUpperCase();
        println(EMPTY_LINE.toString());
        if (!(student.equals("YELLOW") || student.equals("BLUE") || student.equals("GREEN") || student.equals("RED") || student.equals("PINK")))
            error();
        println(CURSOR_TO_START.toString() + CLEAR_TERMINAL);
        return student;
    }

    /**
     * Check on island
     *
     * @param input  scanner
     * @param island previous choice
     * @return current choice
     */
    private int getIsland(@NotNull Scanner input, int island) {
        print(PROMPT);
        String is = input.nextLine();
        if (controlsInput(is)) island = Integer.parseInt(is);
        println(EMPTY_LINE.toString());
        if (island < 0 || island >= gameManager.getModel().getGameBoard().getIslands().size()) error();
        println(CURSOR_TO_START.toString() + CLEAR_TERMINAL);
        return island;
    }

    /**
     * Sommelier's view
     *
     * @param input         scanner
     * @param characterCard characterCard
     * @return params
     */
    @Contract("_, _ -> new")
    private @NotNull StudentIntCharacterParameters sommelier(Scanner input, CharacterCard characterCard) {
        String student;
        int island = -1;
        do {
            colorPrintln(CYAN_TEXT + "CHOOSE A STUDENT TO MOVE (color): " + WHITE_TEXT);
            for (int i = 0; i < ((SommelierCharacter) characterCard).getStudents().size(); i++) {
                println("Student " + i + " : " + ((SommelierCharacter) characterCard).getStudents().get(i));
            }
            student = getString(input);
        } while (!(student.equals("YELLOW") || student.equals("BLUE") || student.equals("GREEN") || student.equals("RED") || student.equals("PINK")));

        do {
            colorPrintln(CYAN_TEXT + "CHOOSE AN ISLAND TO MOVE TO (0 to " + (gameManager.getModel().getGameBoard().getIslands().size() - 1) + "): " + WHITE_TEXT);
            island = getIsland(input, island);
        } while (island < 0 || island >= gameManager.getModel().getGameBoard().getIslands().size());

        return new StudentIntCharacterParameters(SPColor.valueOf(student), island);
    }

    /**
     * Herbalist's view
     * Messenger's view
     *
     * @param input scanner
     * @return params
     */
    @Contract("_ -> new")
    private @NotNull IntCharacterParameters herbalistMessenger(@NotNull Scanner input) {
        int island2 = -1;
        do {
            colorPrintln(CYAN_TEXT + "CHOOSE AN ISLAND (0 to " + (gameManager.getModel().getGameBoard().getIslands().size() - 1) + "): " + WHITE_TEXT);
            island2 = getIsland(input, island2);
        } while (island2 < 0 || island2 >= gameManager.getModel().getGameBoard().getIslands().size());
        return new IntCharacterParameters(island2);
    }

    /**
     * Joker's view
     *
     * @param input         scanner
     * @param characterCard characterCard
     * @return params
     */
    @Contract("_, _ -> new")
    private @NotNull StudentsCharacterParameters joker(Scanner input, CharacterCard characterCard) {
        ArrayList<SPColor> students = new ArrayList<>();
        int st = -2;
        do {
            colorPrintln(CYAN_TEXT + "CHOOSE A STUDENT TO MOVE TO YOUR ENTRANCE (1 to " + ((JokerCharacter) characterCard).getStudents().size() + "): " + WHITE_TEXT);
            colorPrintln(CYAN_TEXT + "ENTER 0 TO END" + WHITE_TEXT);
            println(EMPTY_LINE.toString());
            for (int i = 0; i < ((JokerCharacter) characterCard).getStudents().size(); i++) {
                println("Student " + (i + 1) + ": " + ((JokerCharacter) characterCard).getStudents().get(i));
            }
            print(PROMPT);
            String is = input.nextLine();
            if (controlsInput(is)) {
                st = Integer.parseInt(is);
                if (st > 0 && st <= ((JokerCharacter) characterCard).getStudents().size()) {
                    students.add(((JokerCharacter) characterCard).getStudents().get(st - 1));
                    ((JokerCharacter) characterCard).getStudents().remove(st - 1);
                }
            }
            println(EMPTY_LINE.toString());
            if (!(st > -1 && (st <= ((JokerCharacter) characterCard).getStudents().size() + 1))) error();
            println(CURSOR_TO_START.toString() + CLEAR_TERMINAL);
        } while (st != 0 && students.size() < 3);

        int size = students.size();

        do {
            colorPrintln(CYAN_TEXT + "CHOOSE A STUDENT TO MOVE FROM YOUR ENTRANCE (1 to " +
                    gameManager.getModel().getGameBoard().getPlayerBoard(gameManager.getMyPlayer().getId()).getStudents().size() + "): " + WHITE_TEXT);
            int st2 = chooseStudentFromEntrance(input, students);
            if (!(st2 > 0 && (st2 <= gameManager.getModel().getGameBoard().getPlayerBoard(gameManager.getMyPlayer().getId()).getStudents().size() + 1)))
                error();
            println(CURSOR_TO_START.toString() + CLEAR_TERMINAL);
        } while (students.size() < (size * 2));
        return new StudentsCharacterParameters(students);
    }

    /**
     * Merchant's view
     * Sinister's view
     *
     * @param input scanner
     * @return params
     */
    @Contract("_ -> new")
    private @NotNull StudentsCharacterParameters merchantSinister(Scanner input) {
        String student2;
        do {
            colorPrintln(CYAN_TEXT + "CHOOSE A COLOR: " + WHITE_TEXT);
            student2 = chooseColor(input);
        } while (!(student2.equals("YELLOW") || student2.equals("BLUE") || student2.equals("GREEN") || student2.equals("RED") || student2.equals("PINK")));
        return new StudentsCharacterParameters(new ArrayList<>(Collections.singleton(SPColor.valueOf(student2))));
    }

    /**
     * Musician's view
     *
     * @param input scanner
     * @return params
     */
    @Contract("_ -> new")
    private @NotNull StudentsCharacterParameters musician(Scanner input) {
        ArrayList<SPColor> students2 = new ArrayList<>();
        int st3 = -2;
        do {
            colorPrintln(CYAN_TEXT + "CHOOSE A STUDENT TO MOVE TO YOUR ENTRANCE (1 to " +
                    gameManager.getModel().getGameBoard().getPlayerBoard(gameManager.getMyPlayer().getId()).getCanteen().getStudents().size() + "): " + WHITE_TEXT);
            colorPrintln(CYAN_TEXT + "ENTER 0 TO END" + WHITE_TEXT);
            println(EMPTY_LINE.toString());
            for (int i = 0; i < gameManager.getModel().getGameBoard().getPlayerBoard(gameManager.getMyPlayer().getId()).getCanteen().getStudents().size(); i++) {
                println("Student " + (i + 1) + ": " + gameManager.getModel().getGameBoard().getPlayerBoard(gameManager.getMyPlayer().getId()).getCanteen().getStudents().get(i));
            }
            print(PROMPT);
            String is = input.nextLine();
            if (controlsInput(is)) {
                st3 = Integer.parseInt(is);
                if (st3 > 0 && st3 <= gameManager.getModel().getGameBoard().getPlayerBoard(gameManager.getMyPlayer().getId()).getCanteen().getStudents().size()) {
                    students2.add(gameManager.getModel().getGameBoard().getPlayerBoard(gameManager.getMyPlayer().getId()).getCanteen().getStudents().get(st3 - 1));
                    gameManager.getModel().getGameBoard().getPlayerBoard(gameManager.getMyPlayer().getId()).getCanteen().getStudents().remove(st3 - 1);
                }
            }
            println(EMPTY_LINE.toString());
            if (!(st3 > -1 && (st3 <= gameManager.getModel().getGameBoard().getPlayerBoard(gameManager.getMyPlayer().getId()).getCanteen().getStudents().size() + 1)))
                error();
            println(CURSOR_TO_START.toString() + CLEAR_TERMINAL);
        } while (st3 != 0 && students2.size() < 2);

        int size2 = students2.size();

        do {
            colorPrintln(CYAN_TEXT + "CHOOSE A STUDENT TO MOVE TO YOUR CANTEEN (1 to " +
                    gameManager.getModel().getGameBoard().getPlayerBoard(gameManager.getMyPlayer().getId()).getStudents().size() + "): " + WHITE_TEXT);
            int st2 = chooseStudentFromEntrance(input, students2);
            if (!(st2 > 0 && (st2 <= gameManager.getModel().getGameBoard().getPlayerBoard(gameManager.getMyPlayer().getId()).getStudents().size() + 1)))
                error();
            println(CURSOR_TO_START.toString() + CLEAR_TERMINAL);
        } while (students2.size() < (size2 * 2));
        return new StudentsCharacterParameters(students2);
    }

    /**
     * Lady's view
     *
     * @param input         scanner
     * @param characterCard characterCard
     * @return params
     */
    @Contract("_, _ -> new")
    private @NotNull StudentsCharacterParameters lady(Scanner input, CharacterCard characterCard) {
        String student3;
        do {
            colorPrintln(CYAN_TEXT + "CHOOSE A STUDENT TO MOVE (color): " + WHITE_TEXT);
            for (int i = 0; i < ((LadyCharacter) characterCard).getStudents().size(); i++) {
                println("Student " + i + " : " + ((LadyCharacter) characterCard).getStudents().get(i));
            }
            student3 = chooseColor(input);
        } while (!(student3.equals("YELLOW") || student3.equals("BLUE") || student3.equals("GREEN") || student3.equals("RED") || student3.equals("PINK")));
        return new StudentsCharacterParameters(new ArrayList<>(Collections.singleton(SPColor.valueOf(student3))));
    }

    /**
     * Get a color
     *
     * @param input scanner
     * @return a color
     */
    @NotNull
    private String chooseColor(@NotNull Scanner input) {
        return getString(input);
    }

    /**
     * Get a student from the entrance
     *
     * @param input    scanner
     * @param students all the students
     * @return the student
     */
    private int chooseStudentFromEntrance(Scanner input, ArrayList<SPColor> students) {
        int st2 = -1;
        println(EMPTY_LINE.toString());
        for (int i = 0; i < gameManager.getModel().getGameBoard().getPlayerBoard(gameManager.getMyPlayer().getId()).getStudents().size(); i++) {
            println("Student " + (i + 1) + ": " + gameManager.getModel().getGameBoard().getPlayerBoard(gameManager.getMyPlayer().getId()).getStudents().get(i));
        }
        print(PROMPT);
        String is = input.nextLine();
        if (controlsInput(is)) {
            st2 = Integer.parseInt(is);
            if (st2 > 0 && st2 <= gameManager.getModel().getGameBoard().getPlayerBoard(gameManager.getMyPlayer().getId()).getStudents().size()) {
                students.add(gameManager.getModel().getGameBoard().getPlayerBoard(gameManager.getMyPlayer().getId()).getStudents().get(st2 - 1));
                gameManager.getModel().getGameBoard().getPlayerBoard(gameManager.getMyPlayer().getId()).getStudents().remove(st2 - 1);
            }
        }
        println(EMPTY_LINE.toString());
        return st2;
    }

    /**
     * Student's request
     *
     * @param code   first try / previous error
     * @param reason fail reason
     */
    @Override
    public void beginRequestStudentMove(EvaluateMoveMessage.MoveResponse code, String reason) {

        Scanner input = new Scanner(System.in);
        String student;
        String dest;
        String cloud;
        int nCloud = -1;
        String opt;
        int option = -1;

        printEvaluateStudent(code, reason);

        println(CURSOR_TO_START.toString() + CLEAR_TERMINAL);

        do {
            do {
                println(CURSOR_TO_START.toString() + CLEAR_TERMINAL);
                colorPrintln(CYAN_TEXT + "YOU HAVE TO MOVE A STUDENT. WHAT WOULD YOU SEE?" + WHITE_TEXT);
                println("1 - Your entrance                  ");
                println("2 - Your canteen                   ");
                println("3 - Opponents' canteens            ");
                println("4 - Islands                        ");
                println("5 - None, I have decided what to do");
                println(EMPTY_LINE.toString());
                print(PROMPT);
                opt = input.nextLine();
                if (controlsInput(opt)) option = Integer.parseInt(opt);
                println(EMPTY_LINE.toString());
                if (option < 1 || option > 5) {
                    println(EMPTY_LINE.toString());
                    colorPrintln(RED_TEXT + "NOT A VALID CHOICE" + WHITE_TEXT);
                    println(EMPTY_LINE.toString());
                    println("Enter to retry");
                    input.nextLine();
                }
            } while (option < 1 || option > 5);
            selectMenu(option);
            if (option != 5) {
                println("Enter to continue playing");
                input.nextLine();
            }
            println(CURSOR_TO_START.toString() + CLEAR_TERMINAL);
        } while (option != 5);

        println(EMPTY_LINE.toString());
        printEntrance();
        println(EMPTY_LINE.toString());

        do {
            colorPrintln(CYAN_TEXT + "CHOOSE A STUDENT TO MOVE (color): " + WHITE_TEXT);
            print(PROMPT);
            student = input.nextLine();
            student = student.toUpperCase();
            println(EMPTY_LINE.toString());
            student = completeText(student);
            if (!(student.equals("YELLOW") || student.equals("BLUE") || student.equals("GREEN") || student.equals("RED") || student.equals("PINK")))
                error();
        } while (!(student.equals("YELLOW") || student.equals("BLUE") || student.equals("GREEN") || student.equals("RED") || student.equals("PINK")));
        println(EMPTY_LINE.toString());

        do {
            colorPrintln(CYAN_TEXT + "CHOOSE A DESTINATION (Canteen / Island): " + WHITE_TEXT);
            print(PROMPT);
            dest = input.nextLine();
            dest = dest.toUpperCase();
            println(EMPTY_LINE.toString());
            dest = completeText(dest);
            if (!(dest.equals("CANTEEN") || dest.equals("ISLAND"))) error();
        } while (!(dest.equals("CANTEEN") || dest.equals("ISLAND")));
        println(EMPTY_LINE.toString());

        if (dest.equals("ISLAND")) {
            do {
                colorPrintln(CYAN_TEXT + "CHOOSE AN ISLAND (0 to " + (gameManager.getModel().getGameBoard().getIslands().size() - 1) + "): " + WHITE_TEXT);
                print(PROMPT);
                cloud = input.nextLine();
                if (controlsInput(cloud)) nCloud = Integer.parseInt(cloud);
                println(EMPTY_LINE.toString());
                if (nCloud < 0 || nCloud > (gameManager.getModel().getGameBoard().getIslands().size() - 1)) error();
            } while (nCloud < 0 || nCloud > (gameManager.getModel().getGameBoard().getIslands().size() - 1));
        }
        println(EMPTY_LINE.toString());

        gameManager.endRequestMoveStudent(SPColor.valueOf(student), MoveStudentMessage.Destination.valueOf(dest), nCloud);
    }

    /**
     * Standard error's view
     */
    private void error() {
        Scanner in = new Scanner(System.in);
        colorPrintln(RED_TEXT + "NOT A VALID CHOICE" + WHITE_TEXT);
        println(EMPTY_LINE.toString());
        println("Enter to retry");
        in.nextLine();
        println(CURSOR_TO_LAST_ACTION.toString() + CLEAR_TERMINAL);
    }

    /**
     * Useful method to simplify input
     *
     * @param s input
     * @return complete text
     */
    private String completeText(@NotNull String s) {
        switch (s) {
            case "Y" -> {
                return "YELLOW";
            }
            case "B" -> {
                return "BLUE";
            }
            case "R" -> {
                return "RED";
            }
            case "P" -> {
                return "PINK";
            }
            case "G" -> {
                return "GREEN";
            }
            case "C" -> {
                return "CANTEEN";
            }
            case "I" -> {
                return "ISLAND";
            }
            case "T" -> {
                return "TRUE";
            }
            case "F" -> {
                return "FALSE";
            }
        }

        return s;
    }

    /**
     * Student error's view
     *
     * @param code   error code
     * @param reason fail reason
     */
    private void printEvaluateStudent(EvaluateMoveMessage.MoveResponse code, String reason) {
        if (code == null) return;
        Scanner in = new Scanner(System.in);
        println(EMPTY_LINE.toString());
        switch (code) {
            case MOVE_OK -> colorPrintln(GREEN_TEXT + "MOVE OK" + WHITE_TEXT);
            case MOVE_KO -> colorPrintln(RED_TEXT + "MOVE NOT OK. Reason: " + reason + WHITE_TEXT);
        }

        println(EMPTY_LINE.toString());
        println("Enter to continue");
        in.nextLine();
    }

    /**
     * Entrance's view
     */
    private void printEntrance() {
        colorPrintln(CYAN_TEXT + "YOUR ENTRANCE:" + WHITE_TEXT);
        println(EMPTY_LINE.toString());
        for (SPColor color : SPColor.values()) {
            colorPrintln("You have " + getColor(color).toString() +
                    gameManager.getMyPlayer().getBoard().getStudents().stream().filter(s -> s == color).count() + " " +
                    color.toString().toLowerCase() + WHITE_TEXT + " students");
        }
        println(EMPTY_LINE.toString());
    }

    /**
     * Mother nature's request
     *
     * @param b first try / previous error
     */
    @Override
    public void beginRequestMotherNatureMoves(boolean b) {

        if (b) error();

        Scanner input = new Scanner(System.in);
        String move;
        int moves = -1;

        AssistCard card = gameManager.getModel().getGameBoard().getGraveyard(gameManager.getMyPlayer().getId()).getTopCard();

        println(CURSOR_TO_START.toString() + CLEAR_TERMINAL);

        colorPrintln(CYAN_TEXT + "YOU HAVE TO MOVE MOTHER NATURE" + WHITE_TEXT);
        println(EMPTY_LINE.toString());
        colorPrintln(CYAN_TEXT + "YOU SELECTED THE " + card.getAnimal() + " WHIT A MAXIMUM AMOUNT OF MOVES OF " + card.getMoves() + WHITE_TEXT);
        println(EMPTY_LINE.toString());

        printIslands();

        for (Player p : gameManager.getModel().getPlayers()) {
            StringBuilder s = new StringBuilder();
            s.append(p.getName()).append(" owns: ");
            for (SPColor professor : p.getBoard().getCanteen().getProfessors()) {
                s.append(getColor(professor).toString()).append(professor).append(WHITE_TEXT).append(" ");
            }
            if (p.getBoard().getCanteen().getProfessors().size() == 0) {
                s.append("no ");
            }
            s.append("professors");
            colorPrintln(String.valueOf(s));
        }
        println(EMPTY_LINE.toString());

        int maxMoves = gameManager.getMoves();

        do {
            colorPrintln(CYAN_TEXT + "CHOOSE A NUMBER OF MOVES (1 to " + maxMoves + "): " + WHITE_TEXT);
            print(PROMPT);
            move = input.nextLine();
            if (controlsInput(move)) moves = Integer.parseInt(move);
            println(EMPTY_LINE.toString());
            if (moves < 1 || moves > maxMoves) error();
        } while (moves < 1 || moves > maxMoves);

        gameManager.endRequestMotherNatureMoves(moves);
    }

    /**
     * Islands' view
     */
    private void printIslands() {
        for (int i = 0; i < gameManager.getModel().getGameBoard().getIslands().size(); i++) {
            int size = gameManager.getModel().getGameBoard().getIsland(i).getNTowers();
            if (size == 0)
                size = 1;
            colorPrintln(CYAN_TEXT + "ISLAND " + i + ", size " + size + " :" + WHITE_TEXT);
            StringBuilder s = new StringBuilder();
            for (SPColor color : SPColor.values()) {
                s.append(getColor(color).toString())
                        .append(gameManager.getModel().getGameBoard().getIslands().get(i).getStudents().stream().filter(st -> st == color).count())
                        .append(" ").append(color).append(WHITE_TEXT);
                if (color != SPColor.BLUE) s.append(" ; ");
            }
            colorPrintln(String.valueOf(s));
            if (gameManager.getModel().getGameBoard().getIslands().get(i).getOwner() != null) {
                int finalI = i;
                //noinspection OptionalGetWithoutIsPresent
                println(gameManager.getModel().getPlayers().stream()
                        .filter(p -> p.getPlayerTowersColor() == gameManager.getModel().getGameBoard().getIslands().get(finalI)
                                .getOwner()).findFirst().get().getName() + " controls that island");
            } else {
                println("No one controls that island");
            }
            if (gameManager.getModel().getGameBoard().getIslands().get(i).getBans() > 0) {
                println(EMPTY_LINE.toString());
                println("This island has " + gameManager.getModel().getGameBoard().getIsland(i).getBans() + " bans");
            }
        }
        for (int i = 0; i < gameManager.getModel().getGameBoard().getIslands().size(); i++) {
            if (gameManager.getModel().getGameBoard().getIslands().get(i).getMotherNature()) {
                println(EMPTY_LINE.toString());
                colorPrintln(CYAN_TEXT + "Mother nature is on the island " + i + WHITE_TEXT);
            }
        }
        println(EMPTY_LINE.toString());
    }

    /**
     * Cloud's request
     *
     * @param b first try / previous error
     */
    @Override
    public void beginRequestCloud(boolean b) {

        if (b) error();

        Scanner input = new Scanner(System.in);
        String clouds;
        int cloud = -1;
        int j;


        if (gameManager.getModel().getGameBoard().getClouds().stream().noneMatch(c -> c.getStudents().size() > 0)) {
            gameManager.endRequestCloud(0);
            return;
        }

        println(CURSOR_TO_START.toString() + CLEAR_TERMINAL);

        colorPrintln(CYAN_TEXT + "YOU HAVE TO CHOOSE A CLOUD" + WHITE_TEXT);
        println(EMPTY_LINE.toString());

        for (int i = 0; i < gameManager.getModel().getGameBoard().getClouds().size(); i++) {
            if (gameManager.getModel().getGameBoard().getClouds().get(i).getStudents().isEmpty()) {
                println("Cloud " + i + " is empty");
            } else {
                StringBuilder s = new StringBuilder();
                s.append("Cloud ").append(i).append(" contains: ");
                if (gameManager.getModel().getPlayers().size() == 3) {
                    j = 4;
                } else {
                    j = 3;
                }
                SPColor student;
                for (int k = 0; k < j - 1; k++) {
                    student = gameManager.getModel().getGameBoard().getClouds().get(i).getStudents().get(k);
                    s.append(getColor(student).toString()).append(gameManager.getModel().getGameBoard().getClouds().get(i).getStudents().get(k)).append(WHITE_TEXT).append(" , ");
                }
                student = gameManager.getModel().getGameBoard().getClouds().get(i).getStudents().get(j - 1);
                s.append(getColor(student).toString()).append(gameManager.getModel().getGameBoard().getClouds().get(i).getStudents().get(j - 1)).append(WHITE_TEXT);
                colorPrintln(String.valueOf(s));
            }
            println(EMPTY_LINE.toString());
        }
        println(EMPTY_LINE.toString());

        do {
            colorPrintln(CYAN_TEXT + "CHOOSE A CLOUD (0 to " + (gameManager.getModel().getGameBoard().getClouds().size() - 1) + "): " + WHITE_TEXT);
            print(PROMPT);
            clouds = input.nextLine();
            if (controlsInput(clouds)) cloud = Integer.parseInt(clouds);
            println(EMPTY_LINE.toString());
            if (cloud < 0 || cloud > (gameManager.getModel().getGameBoard().getClouds().size() - 1)) error();
        } while (cloud < 0 || cloud > (gameManager.getModel().getGameBoard().getClouds().size() - 1));
        println(EMPTY_LINE.toString());

        gameManager.endRequestCloud(cloud);
    }

    /**
     * Useful only in GUI
     *
     * @param model game's model
     */
    @Override
    public void modelUpdated(Game model) {

    }

    /**
     * Turn change's view
     *
     * @param currentPlayerTurn current player
     */
    @Override
    public void turnChanged(Player currentPlayerTurn) {
        Scanner in = new Scanner(System.in);
        println(CURSOR_TO_START.toString() + CLEAR_TERMINAL);
        if (currentPlayerTurn == gameManager.getMyPlayer()) {
            java.awt.Toolkit.getDefaultToolkit().beep();
            colorPrintln(CYAN_TEXT + "YOUR TURN" + WHITE_TEXT);
            println(EMPTY_LINE.toString());
            println("Enter to start playing");
            in.nextLine();
            println(CURSOR_TO_START.toString() + CLEAR_TERMINAL);
        } else {
            colorPrintln(CYAN_TEXT + "CURRENT PLAYER: " + currentPlayerTurn.getName() + WHITE_TEXT);
            println(EMPTY_LINE.toString());
        }
    }

    /**
     * End game's view
     *
     * @param winner winner
     */
    @Override
    public void gameEnded(Player winner) {
        if (winner == gameManager.getMyPlayer()) {
            colorPrintln(GREEN_TEXT + "YOU WIN" + WHITE_TEXT);
        } else {
            colorPrintln(RED_TEXT + "YOU LOSE" + WHITE_TEXT);
        }
        println(EMPTY_LINE.toString());
        colorPrintln(RED_TEXT + "CLOSING GAME..." + WHITE_TEXT);
        println(EMPTY_LINE.toString());
        gameManager.disconnect();

        System.exit(0);
    }

    public String getServerIP() {
        return serverIP;
    }

    public int getServerPort() {
        return serverPort;
    }

    public String getNickname() {
        return nickname;
    }

    public Player.Magician getMagician() {
        return magician;
    }

    public GameManager getGameManager() {
        return gameManager;
    }
}
