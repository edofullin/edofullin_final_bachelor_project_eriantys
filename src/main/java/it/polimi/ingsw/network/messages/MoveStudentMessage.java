package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.model.SPColor;

public class MoveStudentMessage extends Message {

    final private SPColor student;
    final private Destination destination;
    final private int nIsland;

    public MoveStudentMessage(SPColor student, Destination destination, int n) {
        super();
        this.student = student;
        this.destination = destination;
        this.nIsland = n;
    }

    public MoveStudentMessage(long messageID, SPColor student, Destination destination, int n) {
        super(messageID);
        this.student = student;
        this.destination = destination;
        this.nIsland = n;
    }

    public SPColor getStudent() {
        return this.student;
    }

    public Destination getDestination() {
        return this.destination;
    }

    public int getNIsland() {
        return this.nIsland;
    }

    public enum Destination {
        CANTEEN,
        ISLAND
    }
}
