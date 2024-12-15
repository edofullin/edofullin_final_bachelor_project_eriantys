package it.polimi.ingsw.network.messages;

public class ChooseCloudMessage extends Message {

    final private int cloudID;

    public ChooseCloudMessage(int cloudID) {
        this.cloudID = cloudID;
    }

    public ChooseCloudMessage(long messageId, int cloudID) {
        super(messageId);
        this.cloudID = cloudID;
    }

    public int getCloudID() {
        return cloudID;
    }
}
