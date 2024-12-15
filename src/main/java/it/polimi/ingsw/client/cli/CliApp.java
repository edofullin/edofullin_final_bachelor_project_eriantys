package it.polimi.ingsw.client.cli;

import java.io.IOException;

public class CliApp {
    public static CliView cliView;
    public static int width;

    public static void main(String[] args) throws IOException {

        width = org.jline.terminal.TerminalBuilder.terminal().getWidth();
        if (width == 0) width = 80;

        start();
    }

    public static void start() throws IOException {
        cliView = new CliView();

        cliView.start();
    }
}
