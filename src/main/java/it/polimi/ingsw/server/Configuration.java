package it.polimi.ingsw.server;

import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Objects;

/**
 * Main Server configuration
 */
public class Configuration {

    private static Configuration globalConfig = null;

    private final int clientKeepAliveInterval = 30;
    private final int clientKeepAliveDead = 60;

    private final boolean clientBroadcasterDefault = true;
    private final boolean serverBroadcasterEnabled = true;

    private final int broadcasterInterval = 5;

    private final boolean enableDiskSave = true;

    public static Configuration getConfiguration() {

        if (globalConfig == null)
            loadConfiguration();

        return globalConfig;
    }

    private static void loadConfiguration() {
        Reader reader = new InputStreamReader(Objects.requireNonNull(Configuration.class.getClassLoader().getResourceAsStream("config.json")));
        Gson gson = new Gson();

        globalConfig = gson.fromJson(reader, Configuration.class);
    }

    public int getClientKeepAliveInterval() {
        return clientKeepAliveInterval;
    }

    public int getClientKeepAliveDead() {
        return clientKeepAliveDead;
    }

    public boolean isEnableDiskSave() {
        return enableDiskSave;
    }

    public long getBroadcasterInterval() {
        return broadcasterInterval;
    }

    public boolean isClientBroadcasterDefault() {
        return clientBroadcasterDefault;
    }

    public boolean isServerBroadcasterEnabled() {
        return serverBroadcasterEnabled;
    }
}
