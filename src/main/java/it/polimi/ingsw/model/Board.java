package it.polimi.ingsw.model;

import it.polimi.ingsw.Support;
import it.polimi.ingsw.model.characters.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class Board {

    private final List<PlayerBoard> playerBoards;
    private final List<CardStack> graveyards;
    private final List<Archipelago> islands;
    private final List<Cloud> clouds;
    private final List<SPColor> pouch;
    private final List<SPColor> professors;
    private final List<CharacterCard> characterCards;
    private int coins;

    public Board(@NotNull Board cb) {
        this.playerBoards = cb.playerBoards.stream().map(PlayerBoard::new).collect(Collectors.toList());
        this.graveyards = cb.graveyards.stream().map(CardStack::new).collect(Collectors.toList());
        this.islands = cb.islands.stream().map(Archipelago::new).collect(Collectors.toList());
        this.clouds = cb.clouds.stream().map(Cloud::new).collect(Collectors.toList());
        this.pouch = new ArrayList<>(cb.pouch);
        this.professors = new ArrayList<>(cb.pouch);
        this.characterCards = new ArrayList<>(cb.characterCards);
        this.coins = cb.coins;
    }

    public Board(@NotNull List<Player> players) {
        this.playerBoards = new ArrayList<>();
        this.graveyards = new ArrayList<>();
        this.islands = new ArrayList<>();
        this.clouds = new ArrayList<>();
        this.pouch = generatePouch();
        this.professors = new ArrayList<>();
        this.characterCards = new ArrayList<>();

        for (Player p : players) {
            PlayerBoard pb = new PlayerBoard(p, players.size());
            p.setBoard(pb);
            playerBoards.add(pb);

            CardStack cs = new CardStack(p);
            graveyards.add(cs);
        }

        ArrayList<SPColor> initialPouch = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            initialPouch.add(SPColor.values()[i]);
            initialPouch.add(SPColor.values()[i]);
        }

        for (int i = 0; i < 12; i++) {
            Archipelago ar = new Archipelago();
            if (i != 0 && i != 6) {
                Random rnd = new Random();
                int index = rnd.nextInt(0, initialPouch.size());
                ar.addStudent(initialPouch.get(index));
                initialPouch.remove(index);
            }
            islands.add(ar);
        }
        islands.get(0).setMotherNature(true);

        for (int i = 0; i < players.size(); i++) {
            Cloud cl = new Cloud();
            clouds.add(cl);
        }

        professors.addAll(Arrays.asList(SPColor.values()));

        coins = -1;
    }

    /**
     * Replaces current character cards, used FOR DEBUG ONLY
     *
     * @param characterCards the new character cards
     */
    public void replaceCharacterCards(List<CharacterCard> characterCards) {
        this.characterCards.clear();
        this.characterCards.addAll(characterCards);
    }

    /**
     * Create a random pouch
     *
     * @return a pouch
     */
    private @NotNull List<SPColor> generatePouch() {
        List<SPColor> students = new ArrayList<>();
        for (int i = 0; i < 120; i++) {
            students.add(SPColor.values()[i % 5]);
        }
        Collections.shuffle(students);
        return students;
    }

    /**
     * Get all the player's boards
     *
     * @return all the player's boards
     */
    public List<PlayerBoard> getPlayerBoards() {
        return this.playerBoards;
    }

    /**
     * Get a player's board by player
     *
     * @param p the player
     * @return the player's board
     */
    public PlayerBoard getPlayerBoard(Player p) {
        return this.playerBoards.stream().filter(pb -> pb.getOwner() == p).findFirst().orElse(null);
    }

    /**
     * Get a player's board by index
     *
     * @param index player's index
     * @return the player's board
     */
    public PlayerBoard getPlayerBoard(int index) {
        return this.playerBoards.get(index);
    }

    /**
     * Get all the graveyards
     *
     * @return all the graveyards
     */
    public List<CardStack> getGraveyards() {
        return this.graveyards;
    }

    /**
     * Get a graveyard by player
     *
     * @param p the player
     * @return player's graveyard
     */
    public CardStack getGraveyard(Player p) {
        return this.graveyards.stream().filter(gr -> gr.getOwner() == p).findFirst().orElse(null);
    }

    /**
     * Get a graveyard by index
     *
     * @param index player's index
     * @return the player's graveyard
     */
    public CardStack getGraveyard(int index) {
        return this.graveyards.get(index);
    }

    /**
     * Get all the islands
     *
     * @return all the islands
     */
    public List<Archipelago> getIslands() {
        return this.islands;
    }

    /**
     * Get the island whit MotherNature
     *
     * @return island whit MotherNature
     */
    public Archipelago getMotherNatureIsland() {
        return islands.stream().filter(Archipelago::getMotherNature).findFirst().orElse(null);
    }

    /**
     * Get an island by index
     *
     * @param index the island
     * @return the island on that index
     */
    public Archipelago getIsland(int index) {
        return this.islands.get(index);
    }

    /**
     * Get player with most influence on island and give it to him
     *
     * @param island island to calculate influence on
     * @return player with most influence on island, null if there's a tie
     */
    public Player updateInfluence(@NotNull Archipelago island) {
        long[] points = new long[getPlayers().size()];
        boolean centaur = isActive(CharacterEnum.CENTAUR);

        if (island.getBans() > 0) {
            island.removeBan();
            ((HerbalistCharacter) getAvailableCharacterByType(CharacterEnum.HERBALIST)).returnBan();
            return null;
        }

        SPColor ban = null;

        if (isActive(CharacterEnum.MERCHANT)) {
            ban = ((MerchantCharacter) getAvailableCharacterByType(CharacterEnum.MERCHANT)).getStudent();
        }

        for (int i = 0; i < points.length; i++) {
            Player p = getPlayers().get(i);

            if (p.getPlayerTowersColor() == island.getOwner() && !centaur) {
                points[i] += island.getNTowers();
            }

            if (isActive(CharacterEnum.KNIGHT) && p.equals(((KnightCharacter) getActiveCharacter()).getPlayer())) {
                points[i] += 2;
            }

            List<SPColor> controlledProf = p.getBoard().getCanteen().getProfessors();

            for (SPColor prof : controlledProf) {
                if (prof != ban)
                    points[i] += island.getStudents().stream().filter(s -> s == prof).count();
            }

        }

        if (Support.findMaxOrTiePosition(points).size() > 1) return null;

        TowerColor color = getPlayers().get(Support.findMaxOrTiePosition(points).get(0)).getPlayerTowersColor();

        if (island.getOwner() != null && island.getOwner() == color)
            return getPlayers().get(Support.findMaxOrTiePosition(points).get(0));

        if (island.getOwner() != null) {
            int towers = island.getNTowers();
            for (int i = 0; i < towers; i++) {
                getPlayerBoard(getPlayers().stream().filter(p -> p.getPlayerTowersColor() == island.getOwner()).findFirst().orElse(null)).addTower(island.getOwner());
            }
        }

        island.setOwner(color);

        int towers = island.getNTowers();
        for (int i = 0; i < towers; i++) {
            getPlayerBoard(getPlayers().stream().filter(p -> p.getPlayerTowersColor() == island.getOwner()).findFirst().orElse(null)).removeTower(island.getOwner());
        }

        int prev;
        int next;

        if (islands.indexOf(island) == 0) {
            next = islands.indexOf(island) + 1;
            prev = islands.size() - 1;
        } else if (islands.indexOf(island) == (islands.size() - 1)) {
            next = 0;
            prev = islands.indexOf(island) - 1;
        } else {
            next = islands.indexOf(island) + 1;
            prev = islands.indexOf(island) - 1;
        }

        if (island.getOwner() == getIsland(next).getOwner()) {
            joinIslands(island, getIsland(next));
            if (next == 0 || next == 1)
                prev--;
        }

        if (island.getOwner() == getIsland(prev).getOwner()) {
            joinIslands(island, getIsland(prev));
        }

        return getPlayers().get(Support.findMaxOrTiePosition(points).get(0));

    }

    /**
     * Call the right method
     *
     * @param islNum the island
     * @return the new owner
     */
    public Player updateInfluence(int islNum) {
        return this.updateInfluence(this.islands.get(islNum));
    }

    /**
     * Move mother nature
     *
     * @param steps steps to be made
     */
    public void moveMotherNature(int steps) {
        @SuppressWarnings("OptionalGetWithoutIsPresent")
        Archipelago isola = islands.stream().filter(Archipelago::getMotherNature).findFirst().get();
        int i = islands.indexOf(isola);
        isola.setMotherNature(false);
        i = (i + steps) % (islands.size());
        islands.get(i).setMotherNature(true);
    }

    /**
     * Join two islands
     *
     * @param island1 island to join - on
     * @param island2 island to join - from
     */
    public void joinIslands(Archipelago island1, @NotNull Archipelago island2) {
        if (island2.getMotherNature()) island1.setMotherNature(true);
        island1.modifyNTowers(island2.getNTowers());
        for (SPColor student : island2.getStudents()) {
            island1.addStudent(student);
        }
        island1.addBans(island2.getBans());
        this.islands.remove(island2);
    }

    /**
     * Get all the clouds
     *
     * @return all the clouds
     */
    public List<Cloud> getClouds() {
        return this.clouds;
    }

    /**
     * Get a cloud by index
     *
     * @param index the cloud
     * @return the cloud with that index
     */
    public Cloud getCloud(int index) {
        return this.clouds.get(index);
    }

    /**
     * Fill all the clouds with students
     */
    public void addStudentsClouds() {
        SPColor student;
        for (Cloud cloud : clouds) {
            switch (clouds.size()) {
                case 2:
                    for (int i = 0; i < 3; i++) {
                        student = extractStudentFromPouch();
                        if (student != null) cloud.addStudent(student);
                    }
                    break;
                case 3:
                    for (int i = 0; i < 4; i++) {
                        student = extractStudentFromPouch();
                        if (student != null) cloud.addStudent(student);
                    }
                    break;
            }
        }
    }

    /**
     * Empty a chosen cloud
     *
     * @param p     the player who chose the cloud
     * @param index the chosen cloud
     */
    public void chooseCloudToEmpty(Player p, int index) {
        while (!clouds.get(index).getStudents().isEmpty()) {
            Support.moveStudent(clouds.get(index).getStudents().get(0), clouds.get(index), getPlayerBoard(p));
        }
    }

    /**
     * Get the pouch
     *
     * @return the pouch
     */
    public List<SPColor> getPouch() {
        return this.pouch;
    }

    /**
     * Get a student from the pouch
     *
     * @return a student from the pouch
     */
    public SPColor extractStudentFromPouch() {
        if (pouch.isEmpty()) return null;
        SPColor result = pouch.get(0);
        pouch.remove(0);
        return result;
    }

    /**
     * Get the professors on the board
     *
     * @return the professors
     */
    public List<SPColor> getProfessors() {
        return this.professors;
    }

    /**
     * Get the payer's board with the professor chosen
     *
     * @param color the professor to search
     * @return the player's board with the professor
     */
    public PlayerBoard findPBByProfessor(SPColor color) {
        for (PlayerBoard pb : this.playerBoards)
            if (pb.getCanteen().getProfessors().stream().anyMatch(prof -> prof == color))
                return pb;
        return null;
    }

    /**
     * Update all the professors
     */
    public void updateProfessors() {

        for (SPColor color : SPColor.values()) {

            PlayerBoard oldOwner = findPBByProfessor(color);

            long[] points = new long[playerBoards.size()];

            for (int i = 0; i < points.length; i++) {
                points[i] = playerBoards.get(i).getCanteen().getStudents().stream().filter(s -> s == color).count();
            }

            int newIndex = Support.findMaxOrTiePosition(points).get(0);
            if (Support.findMaxOrTiePosition(points).size() == 1) {
                PlayerBoard newOwner = playerBoards.get(newIndex);
                if (oldOwner == null) {
                    professorBoardToPlayer(newOwner, color);
                } else {
                    professorPlayerToPlayer(oldOwner, newOwner, color);
                }
            }

            if (isActive(CharacterEnum.CHEF) && Support.findMaxOrTiePosition(points).size() > 1 && points[Support.findMaxOrTiePosition(points).get(0)] > 0) {
                int currIndex = ((ChefCharacter) getAvailableCharacterByType(CharacterEnum.CHEF)).getPlayer().getId();
                if (Support.findMaxOrTiePosition(points).contains(currIndex)) {
                    PlayerBoard newOwner = playerBoards.get(currIndex);
                    if (oldOwner == null) {
                        professorBoardToPlayer(newOwner, color);
                    } else {
                        professorPlayerToPlayer(oldOwner, newOwner, color);
                    }
                }
            }
        }
    }

    /**
     * Move a professor from the board to a player's canteen
     *
     * @param board the player's board where to move the professor
     * @param color the professor to move
     */
    public void professorBoardToPlayer(@NotNull PlayerBoard board, SPColor color) {
        professors.remove(color);
        board.getCanteen().addProfessor(color);
    }

    /**
     * Move a professor from a player to another
     *
     * @param oldOwner the player's board where to remove the professor
     * @param newOwner the player's board where to move the professor
     * @param color    the professor to move
     */
    public void professorPlayerToPlayer(@NotNull PlayerBoard oldOwner, @NotNull PlayerBoard newOwner, SPColor color) {
        oldOwner.getCanteen().removeProfessor(color);
        newOwner.getCanteen().addProfessor(color);
    }

    /**
     * Get the character's cards
     *
     * @return the character's cards
     */
    public List<CharacterCard> getCharacterCards() {
        return this.characterCards;
    }

    /**
     * Initializes the characters
     */
    public void initCharacters() {
        FactoryCharacterCard factoryCharacterCard = new FactoryCharacterCard();
        Random rnd = new Random();
        List<CharacterEnum> allCharacters = new ArrayList<>(List.of(CharacterEnum.values()));

        for (int i = 0; i < 3; i++) {
            int index = rnd.nextInt(0, allCharacters.size() - 1);
            CharacterEnum extracted = allCharacters.get(index);

            this.characterCards.add(
                    factoryCharacterCard.createCharacter(extracted)
            );

            allCharacters.remove(extracted);
        }
    }

    /**
     * Get active character
     *
     * @return active character
     */
    public CharacterCard getActiveCharacter() {
        return characterCards.stream().filter(CharacterCard::isActive).findFirst().orElse(null);
    }

    /**
     * Return if a character is active
     *
     * @param characterEnum the character
     * @return if it is active
     */
    public boolean isActive(CharacterEnum characterEnum) {
        if (getAvailableCharacterByType(characterEnum) == null) return false;
        if (!(getActiveCharacter() == null)) return getActiveCharacter().getEnumType() == characterEnum;
        return false;
    }

    /**
     * Transforms the enum in the character card
     *
     * @param ce the character enum
     * @return the character card
     */
    public CharacterCard getAvailableCharacterByType(@NotNull CharacterEnum ce) {

        String name = ce.toString().toLowerCase() + "Character";
        String cap = name.substring(0, 1).toUpperCase() + name.substring(1);

        for (CharacterCard characterCard : characterCards) {
            if (characterCard.getClass().getSimpleName().equals(cap))
                return characterCard;
        }

        return null;
    }

    /**
     * Get the coins
     *
     * @return the coins
     */
    public int getCoins() {
        return coins;
    }

    /**
     * Modify the coins
     *
     * @param n the coins to be added/removed
     */
    public void modifyCoins(int n) {
        coins += n;
    }

    /**
     * Get all the players
     *
     * @return all the players
     */
    public List<Player> getPlayers() {
        return playerBoards.stream().map(PlayerBoard::getOwner).collect(Collectors.toList());
    }

    /**
     * Only for tests
     */
    public void setCharacterCards() {
        FactoryCharacterCard factoryCharacterCard = new FactoryCharacterCard();

        this.characterCards.add(factoryCharacterCard.createCharacter(CharacterEnum.HERBALIST));
        this.characterCards.get(0).initializeGame(this);
        this.characterCards.add(factoryCharacterCard.createCharacter(CharacterEnum.CENTAUR));
        this.characterCards.get(1).initializeGame(this);
        this.characterCards.add(factoryCharacterCard.createCharacter(CharacterEnum.MERCHANT));
        this.characterCards.get(2).initializeGame(this);
        this.characterCards.add(factoryCharacterCard.createCharacter(CharacterEnum.KNIGHT));
        this.characterCards.get(3).initializeGame(this);
        this.characterCards.add(factoryCharacterCard.createCharacter(CharacterEnum.CHEF));
        this.characterCards.get(4).initializeGame(this);
    }
}
