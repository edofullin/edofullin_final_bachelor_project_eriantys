package it.polimi.ingsw;


import it.polimi.ingsw.client.cli.CliApp;
import it.polimi.ingsw.client.gui.GUIApp;
import it.polimi.ingsw.server.ServerApp;
import org.jetbrains.annotations.NotNull;

import java.util.Scanner;
import java.util.concurrent.Callable;

/**
 * Application startup class
 */
public class App {
    public static void main(String @NotNull [] args) throws Exception {

        Callable<Void> toCall;

        if (args.length > 0) {
            toCall = switch (args[0]) {
                case "--server" -> () -> {
                    ServerApp.main(args);
                    return null;
                };
                case "--gui" -> () -> {
                    GUIApp.main(args);
                    return null;
                };
                case "--cli" -> () -> {
                    CliApp.main(args);
                    return null;
                };
                default -> throw new RuntimeException("");
            };
        } else {
            System.out.println("What do you want to run? [CLI/GUI/Server]");
            Scanner scanner = new Scanner(System.in);
            String resp = scanner.nextLine();
            toCall = switch (resp.toLowerCase()) {
                case "server" -> () -> {
                    ServerApp.main(args);
                    return null;
                };
                case "gui" -> () -> {
                    GUIApp.main(args);
                    return null;
                };
                case "cli" -> () -> {
                    CliApp.main(args);
                    return null;
                };
                default -> throw new RuntimeException("");
            };
        }

        toCall.call();
    }
}
