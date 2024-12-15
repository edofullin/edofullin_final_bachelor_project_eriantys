package it.polimi.ingsw.network.messages;

import com.google.gson.Gson;
import it.polimi.ingsw.Support;

import java.util.Random;

public abstract class Message {

    private long messageId;

    public Message() {
        this(new Random().nextLong());
    }

    public Message(Message message) {
        this(message.getMessageId());
    }

    public Message(long messageId) {
        this.messageId = messageId;
    }

    /**
     * @return Main JSON object for the entire project
     * @apiNote GSON has moved to Support
     */
    public static Gson GSON() {
        return Support.GSON();
    }

    public static Message fromJson(String json) {
        return GSON().fromJson(json, Message.class);
    }

    public String toJson() {
        return GSON().toJson(this, Message.class);
    }

    @Override
    public String toString() {
        return toJson();
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long id) {
        this.messageId = id;
    }
}
