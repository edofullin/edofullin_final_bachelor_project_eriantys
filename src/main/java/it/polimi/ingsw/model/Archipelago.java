package it.polimi.ingsw.model;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Archipelago implements IStudentContainer {

    final private List<SPColor> students;
    private boolean MotherNature;
    private TowerColor owner;
    private int n_towers;

    private int bans;

    public Archipelago(@NotNull Archipelago ca) {
        this.MotherNature = ca.MotherNature;
        this.owner = ca.owner;
        this.n_towers = ca.n_towers;
        this.students = new ArrayList<>(ca.students);
        this.bans = ca.bans;
    }

    public Archipelago() {
        MotherNature = false;
        this.owner = null;
        this.n_towers = 0;
        this.students = new ArrayList<>();
        this.bans = 0;
    }

    /**
     * Get a boolean representing MotherNature
     *
     * @return MotherNature
     */
    public boolean getMotherNature() {
        return this.MotherNature;
    }

    /**
     * Set MotherNature on value
     *
     * @param value to set MotherNature
     */
    public void setMotherNature(boolean value) {
        this.MotherNature = value;
    }

    /**
     * Get owner
     *
     * @return owner
     */
    public TowerColor getOwner() {
        return this.owner;
    }

    /**
     * Set owner
     *
     * @param color the new owner
     */
    public void setOwner(TowerColor color) {
        if (this.owner == null) this.modifyNTowers(1);
        this.owner = color;
    }

    /**
     * Get NTower
     *
     * @return NTower
     */
    public int getNTowers() {
        return this.n_towers;
    }

    /**
     * Modify NTower by n
     *
     * @param n how much is modified
     */
    public void modifyNTowers(int n) {
        this.n_towers += n;
    }

    /**
     * Get Bans
     *
     * @return number of bans
     */
    public int getBans() {
        return this.bans;
    }

    /**
     * Add n bans
     *
     * @param n the number of bans to be added
     */
    public void addBans(int n) {
        this.bans += n;
    }

    /**
     * Remove a ban
     */
    public void removeBan() {
        this.bans--;
    }

    /**
     * Get all students on an archipelago
     *
     * @return all the students
     */
    @Override
    public List<SPColor> getStudents() {
        return this.students;
    }

    public int getStudentsByColor(SPColor color) {
        return (int) students.stream().filter(s -> s == color).count();
    }

    /**
     * Add a student
     *
     * @param student the student to be added
     */
    @Override
    public void addStudent(SPColor student) {
        this.students.add(student);
    }

    /**
     * Remove a student
     *
     * @param student the student to be added
     */
    @Override
    public void removeStudent(SPColor student) {
        this.students.remove(student);
    }

    public String getStringDescription() {
        StringBuilder builder = new StringBuilder();

        if (getOwner() != null) {
            builder.append("OWNER: %s\n".formatted(getOwner().toString()));
        }
        if (getNTowers() != 0) {
            builder.append("%d TOWERS\n".formatted(getNTowers()));
        }

        for (SPColor value : SPColor.values()) {
            if (getStudentsByColor(value) != 0) {
                builder.append("%s: %d\n".formatted(value.toString(), getStudentsByColor(value)));
            }
        }

        if (getBans() > 0) {
            builder.append("\n%d HERBALIST BANS".formatted(getBans()));
        }

        if (getMotherNature()) {
            builder.append("\nMOTHER NATURE IS HERE");
        }

        return builder.toString();
    }
}
