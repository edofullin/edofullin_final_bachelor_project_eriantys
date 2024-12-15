package it.polimi.ingsw.model;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PlayerBoard implements IStudentContainer {
    final private Canteen canteen;
    final private List<TowerColor> towers;
    final private List<SPColor> entrance;
    private Player playerOwner;

    public PlayerBoard(@NotNull PlayerBoard cpb) {
        playerOwner = new Player(cpb.playerOwner);
        this.canteen = new Canteen(cpb.canteen);
        this.towers = new ArrayList<>(cpb.towers);
        this.entrance = new ArrayList<>(cpb.entrance);
    }

    public PlayerBoard(Player player, int num_players) {
        this.playerOwner = player;
        this.canteen = new Canteen();
        this.towers = new ArrayList<>();
        this.entrance = new ArrayList<>();

        int towers = 6;
        if (num_players == 2) towers += 2;

        for (int i = 0; i < towers; i++) {
            TowerColor tower = getOwner().getPlayerTowersColor();
            this.addTower(tower);
        }

        player.setBoard(this);
    }

    /**
     * Get the "owner"
     *
     * @return the "owner"
     */
    public Player getOwner() {
        return this.playerOwner;
    }

    /**
     * Get the canteen
     *
     * @return the canteen
     */
    public Canteen getCanteen() {
        return this.canteen;
    }

    /**
     * Add a tower
     *
     * @param tower the tower to be added
     */
    public void addTower(TowerColor tower) {
        this.towers.add(tower);
    }

    /**
     * Remove a tower
     *
     * @param tower the tower to be removed
     */
    public void removeTower(TowerColor tower) {
        this.towers.remove(tower);
    }

    /**
     * Get the towers
     *
     * @return the towers
     */
    public List<TowerColor> getTowers() {
        return this.towers;
    }

    /**
     * Add a student in the entrance
     *
     * @param student the student to be added
     */
    public void addStudent(SPColor student) {
        this.entrance.add(student);
    }

    /**
     * Remove a student from the entrance
     *
     * @param student the student to be removed
     */
    public void removeStudent(SPColor student) {
        this.entrance.remove(student);
    }

    /**
     * Get the students in the entrance
     *
     * @return the list of students in the entrance
     */
    public List<SPColor> getStudents() {
        return this.entrance;
    }

    /**
     * Set the owner
     *
     * @param playerOwner the owner
     */
    public void setPlayerOwner(Player playerOwner) {
        this.playerOwner = playerOwner;
    }

}
