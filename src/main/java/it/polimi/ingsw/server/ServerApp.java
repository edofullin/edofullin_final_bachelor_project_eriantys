package it.polimi.ingsw.server;

public class ServerApp {

    public static void main(String[] args) {

        // args[1] is lobbysize
        System.out.println("Server: starting server from " + System.getProperty("os.name"));
        Server server = new Server();

        server.startLobbyManager();
        server.waitLobbyManager();
    }

}
