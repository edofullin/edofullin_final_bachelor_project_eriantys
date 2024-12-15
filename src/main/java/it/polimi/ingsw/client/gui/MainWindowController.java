package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.Support;
import it.polimi.ingsw.client.GameManager;
import it.polimi.ingsw.client.IView;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.characters.*;
import it.polimi.ingsw.network.messages.EvaluateMoveMessage;
import it.polimi.ingsw.network.messages.JoinResponseMessage;
import it.polimi.ingsw.network.messages.MoveStudentMessage;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Main window view
 */
public class MainWindowController implements IView {

    int selectedStudents = 0;
    //region fxml bindings
    @FXML  // l' anchor pane principale che contiene tutto
            AnchorPane anchorPaneMain;
    @FXML // griglia principale che divide le entità logiche "padri"
    GridPane gridPaneMain;
    @FXML // utility di gioco
    Pane TopRight;
    @FXML // pb giocatore avversario 1
    Pane OppPB2_3P;
    @FXML // isole
    Pane ISLANDS;
    @FXML // pb giocatore avversario 2
    Pane OppPB1_3p;
    @FXML
    GridPane Clouds;
    // YOUR PB
    @FXML
    GridPane yourPB;
    @FXML
    GridPane ingressoYourPB;
    @FXML
    GridPane mensaYourPB;
    @FXML
    GridPane torriYourPB;
    // PB TOP
    @FXML
    GridPane TopPB;
    // PB LEFT
    @FXML
    GridPane yourCharacters;
    @FXML
    HBox OpponentCharacters;
    @FXML
    ImageView ivAssistLeft;
    @FXML
    ImageView ivAssistRight;
    @FXML
    ImageView ivAssistMiddle;
    @FXML
    Button btnCharacterNO;
    @FXML
    Button btnCharacterNOGFY;
    @FXML
    GridPane gridCloud0;
    @FXML
    GridPane gridCloud1;
    @FXML
    GridPane gridCloud2;
    @FXML
    Label oppName;
    @FXML
    Label yourName;
    @FXML
    Label lblStatus;
    @FXML
    Label lblMyCoins;
    @FXML
    Label lblOppCoins;
    @FXML
    GridPane TopPB1;
    @FXML
    Label opp1Name;
    @FXML
    Label lblOpp1Coins;
    @FXML
    GridPane gp_cloud_0;
    @FXML
    GridPane gp_cloud_1;
    @FXML
    GridPane gp_cloud_2;
    @FXML
    StackPane spMyCoins;
    @FXML
    StackPane spOppCoins;
    @FXML
    StackPane spOpp1Coins;
    private GameManager gameManager;
    private WaitingInputState waitingInputState;
    private SPColor selectedStudent;
    private CharacterParametersBase characterParamaters;
    private CharacterEnum characterType;

    /**
     * Exit button click event handler, disconnects the player and closes the game
     */
    public void btnExitClicked() {
        gameManager.disconnect();
        System.exit(0);
    }

    //endregion

    //region button_events

    public void btnRefreshViewClicked() {
        if (gameManager.getModel() != null)
            redrawModel(gameManager.getModel());
    }

    /**
     * Hand button click handler, opens hand window
     */
    public void btnHandClicked() {
        AssistantSelectorController.createAndDisplayAndWait(gameManager.getMyPlayer().getHand(), false, "Your Assist Cards");
    }

    /**
     * Fullscreen button event handler, enlarges window to full screen size
     */
    public void btnFullscreenClicked() {
        Stage stage = getStage();
        if (stage == null) return;

        stage.setFullScreen(true);
    }

    /**
     * No button event handler, sends message to server that you don't want to use any character in this moment
     */
    public void btnCharacterNoClicked() {
        if (waitingInputState != WaitingInputState.CHARACTER_INPUT) return;
        yourCharacters.setEffect(null);
        setCharacterButtonsVisibility(false);
        gameManager.endRequestCharacter(null, null, false);
    }

    /**
     * No button event handler, sends message to server that you don't want to use any character in this turn
     */

    public void btnCharacterNoGFYClicked() {
        if (waitingInputState != WaitingInputState.CHARACTER_INPUT) return;
        yourCharacters.setEffect(null);
        setCharacterButtonsVisibility(false);
        gameManager.endRequestCharacter(null, null, true);
    }

    //region gui_events
    public void shown() {
        gameManager.registerView(this);
    }

    //endregion

    /**
     * redraws view fitting the new viewport
     */
    public void windowSizeChangedEvent() {
        if (gameManager.getModel() == null) return;
        redrawModel(gameManager.getModel());
    }

    /**
     * sets game manager
     *
     * @param manager is an intance of GameManager
     */
    public void setGameManager(GameManager manager) {
        this.gameManager = manager;
    }

    /**
     * redeaws view matching the changes in the model
     *
     * @param model is an intance of the whole model
     */
    @Override
    public void modelUpdated(Game model) {
        Platform.runLater(() -> {
            redrawModel(model);
        });
    }


    /**
     * Updates the whole gameboard
     *
     * @param model is an instance of the whole model
     */
    private void redrawModel(Game model) {
        yourName.setText("%s\n(%s)".formatted(gameManager.getMyPlayer().getName(), gameManager.getMyPlayer().getPlayerTowersColor().toString()));
        // yourName.setTextFill(gameManager.getMyPlayer().getPlayerTowersColor().toColor());
        yourName.setTextFill(Color.WHITE);

        oppName.setText("%s\n(%s)".formatted(gameManager.getOpponentPlayers().get(0).getName(), gameManager.getOpponentPlayers().get(0).getPlayerTowersColor().toString()));
        // yourName.setTextFill(gameManager.getOpponentPlayers().get(0).getPlayerTowersColor().toColor());
        oppName.setTextFill(Color.WHITE);

        if (opp1Name != null) {
            opp1Name.setText("%s\n(%s)".formatted(gameManager.getOpponentPlayers().get(1).getName(), gameManager.getOpponentPlayers().get(1).getPlayerTowersColor().toString()));
            // opp1name.setTextFill(gameManager.getOpponentPlayers().get(1).getPlayerTowersColor().toColor());
            opp1Name.setTextFill(Color.WHITE);
        }

        spMyCoins.setVisible(model.getExpertMode());
        spOppCoins.setVisible(model.getExpertMode());

        lblMyCoins.setText(String.valueOf(gameManager.getMyPlayer().getCoins()));
        lblOppCoins.setText(String.valueOf(gameManager.getOpponentPlayers().get(0).getCoins()));

        if (lblOpp1Coins != null) {
            spOpp1Coins.setVisible(model.getExpertMode());
            lblOpp1Coins.setText(String.valueOf(gameManager.getOpponentPlayers().get(1).getCoins()));
        }

        updatePB(gameManager.getMyPlayer().getBoard(), yourPB);
        updatePB(gameManager.getOpponentPlayers().get(0).getBoard(), TopPB);

        if (gameManager.getModel().getPlayers().size() == 3) {
            updatePB(gameManager.getOpponentPlayers().get(1).getBoard(), TopPB1);
        }


        // update islands
        updateIslands(model, ISLANDS);
        // update clouds
        updateClouds(model);
        updateLastAss();
        updateCharacterCards(model.getGameBoard(), yourCharacters);
    }

    /**
     * updates Graveyards following the usage of an assistant card
     */
    private void updateLastAss() {

        AssistCard myPlayedCard = gameManager.getMyPlayer().getGraveyard().getTopCard();
        AssistCard oppPlayedCard = gameManager.getOpponentPlayers().get(0).getGraveyard().getTopCard();


        if (myPlayedCard != null)
            ivAssistRight.setImage(GUIResourcesManager.getSingleton().getAssistCardImage(myPlayedCard));

        if (oppPlayedCard != null)
            ivAssistLeft.setImage(GUIResourcesManager.getSingleton().getAssistCardImage(oppPlayedCard));

        if (gameManager.getOpponentPlayers().size() == 2) {
            AssistCard oppPlayedCard1 = gameManager.getOpponentPlayers().get(1).getGraveyard().getTopCard();

            if (oppPlayedCard1 != null) {
                Image opp1Image = GUIResourcesManager.getSingleton().getAssistCardImage(oppPlayedCard1);
                ivAssistMiddle.setImage(opp1Image);
            }
        }
    }

    /**
     * updates the clouds at the end of the turn
     *
     * @param g is an intance of the game
     */
    private void updateClouds(Game g) {

        gridCloud0.getChildren().clear();
        gridCloud1.getChildren().clear();
        if (gridCloud2 != null) {
            gridCloud2.getChildren().clear();
        }
        for (int j = 0; j < g.getGameBoard().getClouds().get(0).getStudents().size(); j++) {

            //  ImageView Iv = getStudentImage(g.getGameBoard().getClouds().get(0).getStudents().get(j));
            ImageView Iv = new ImageView(GUIResourcesManager.getSingleton().getStudentImage(g.getGameBoard().getClouds().get(0).getStudents().get(j)));
            Iv.setFitHeight(25.0);
            Iv.setFitWidth(25.0);
            Iv.setEffect(new DropShadow(8, Color.color(0.3, 0.3, 0.3)));
            gridCloud0.add(Iv, j / 2, j % 2);
        }

        for (int j = 0; j < g.getGameBoard().getClouds().get(1).getStudents().size(); j++) {

            ImageView Iv = new ImageView(GUIResourcesManager.getSingleton().getStudentImage(g.getGameBoard().getClouds().get(1).getStudents().get(j)));
            Iv.setFitHeight(25.0);
            Iv.setFitWidth(25.0);
            Iv.setEffect(new DropShadow(8, Color.color(0.3, 0.3, 0.3)));
            gridCloud1.add(Iv, j / 2, j % 2);
        }

        if (gameManager.getOpponentPlayers().size() == 2) {
            for (int j = 0; j < g.getGameBoard().getClouds().get(2).getStudents().size(); j++) {
                ImageView Iv = new ImageView(GUIResourcesManager.getSingleton().getStudentImage(g.getGameBoard().getClouds().get(2).getStudents().get(j)));
                Iv.setFitHeight(25.0);
                Iv.setFitWidth(25.0);
                Iv.setEffect(new DropShadow(8, Color.color(0.3, 0.3, 0.3)));
                gridCloud2.add(Iv, j / 2, j % 2);
            }
        }
    }

    /**
     * Highights the islands
     *
     * @param effect is either a dwopshadow or null
     */
    private void setEffectToIslands(Effect effect) {
        for (Node child : ISLANDS.getChildren()) {
            if (child.getId().contains("island")) {
                child.setEffect(effect);
            }
        }
    }

    /**
     * highlights  n  islands starting from the current island ( where is mother nature)
     *
     * @param eff is a dropshadow
     * @param n   is the number of island to highlight
     */
    private void setEffectToNextIslands(Effect eff, int n) {
        for (Node child : ISLANDS.getChildren()) {
            if (!child.getId().contains("island")) continue;

            int islnum = Integer.parseInt(child.getId().split("_")[1]);
            int current = gameManager.getCurrentIslandIndex();

            int nIslands = gameManager.getModel().getGameBoard().getIslands().size();
            if ((islnum < current && islnum + nIslands <= current + n) || (islnum > current && islnum <= current + n))
                child.setEffect(eff);
            if (gameManager.getModel().getGameBoard().getIsland(islnum).getMotherNature() && gameManager.getModel().getGameBoard().getIslands().size() <= n)
                child.setEffect(eff);
        }
    }

    private void setEffectToCanteenStudents(Effect eff) {
        for (Node child : mensaYourPB.getChildren()) {
            if (GridPane.getColumnIndex(child) < 10) {
                child.setEffect(eff);
            }
        }
    }

    /**
     * gets the descrition of an island (basically whats in that island)
     *
     * @param island
     */
    private String createIslandTooltip(Archipelago island) {
        return island.getStringDescription();
    }

    /**
     * Draws the islands (buttons) in a semi elliptical shape, sets the description tooltip,
     * adds the owner background if present.
     *
     * @param pane is the islands pane
     * @param game is an instance of the game
     */
    private void updateIslands(Game game, Pane pane) {
        pane.getChildren().removeIf(n -> n.getId().contains("island"));

        double X_c, Y_c;
        double buttonsRadius;
        ImageView islandIV;


        Y_c = pane.getHeight() / 2;
        X_c = pane.getWidth() / 2;
        buttonsRadius = pane.getBoundsInLocal().getHeight() / 2 * 0.85;

        //  Circle centerDebug = new Circle(X_c, Y_c, 5);
        //  pane.getChildren().add(centerDebug);

        for (int i = 0; i < game.getGameBoard().getIslands().size(); i++) {
            Archipelago island = game.getGameBoard().getIslands().get(i);
            // update position
            double degPerIsland = 360.0 / game.getGameBoard().getIslands().size();
            double centerToButton = Math.toRadians(180.0 + i * degPerIsland);

            Button button = new Button();
            islandIV = new ImageView(GUIResourcesManager.getSingleton().getIslandImage());
            islandIV.setPreserveRatio(true);
            islandIV.setFitWidth(80.00);
            islandIV.setFitHeight(80.00);
            button.setPrefSize(40, 40);
            button.setId("island_%d".formatted(i));
            button.setOnMouseClicked(this::islandButtonClicked);
            button.setLayoutX(X_c + (buttonsRadius * Math.cos(centerToButton) - button.getPrefWidth() / 2) * Math.sqrt(2));
            button.setLayoutY(Y_c + (buttonsRadius * Math.sin(centerToButton) - button.getPrefHeight() / 2) * 0.9);
            button.setBackground(null);

            button.setGraphic(islandIV);


            if (island.getOwner() != null)
                button.setStyle("-fx-background-color: " + island.getOwner().toHexColor() + ";");

            if (island.getMotherNature())
                button.setStyle("-fx-background-color: #00ffc7;");

            Tooltip tooltip = new Tooltip(createIslandTooltip(game.getGameBoard().getIsland(i)));

            button.setTooltip(tooltip);


            pane.getChildren().add(button);
        }
    }

    /**
     * updates playerboard view
     *
     * @param PB    reference to playerboard
     * @param gp_PB grid pane containing playerboard
     */
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private void updatePB(PlayerBoard PB, GridPane gp_PB) {

        updateIngresso(PB, PB.getStudents(), (GridPane) gp_PB.getChildren().stream().filter(fx -> fx.getId().startsWith("ingresso")).findFirst().get());
        updateMensa(PB.getCanteen(), (GridPane) gp_PB.getChildren().stream().filter(fx -> fx.getId().startsWith("mensa")).findFirst().get());
        updateTorri(PB, PB.getTowers(), (GridPane) gp_PB.getChildren().stream().filter(fx -> fx.getId().startsWith("torri")).findFirst().get());
        updateProfessor(PB, (GridPane) gp_PB.getChildren().stream().filter(fx -> fx.getId().startsWith("mensa")).findFirst().get());
    }

    /**
     * updates canteen students
     *
     * @param canteen playerboard canteen reference
     * @param mensa   gridpane containing canteen
     */
    private void updateMensa(Canteen canteen, GridPane mensa) {
        long colorStudents = 0;
        mensa.getChildren().clear();

        List<SPColor> students = canteen.getStudents();

        for (SPColor color : SPColor.values()) {
            colorStudents = students.stream().filter(s -> s == color).count();
            for (int i = 0; i < colorStudents; i++) {

                ImageView Iv = new ImageView(GUIResourcesManager.getSingleton().getStudentImage(color));
                Iv.setFitHeight(25);
                Iv.setFitWidth(25);
                Iv.setPickOnBounds(true);

                Iv.setId(color.toString());
                Iv.setOnMouseClicked(e -> {
                    if (waitingInputState != WaitingInputState.CHARACTER_MUSICIAN_CANTEEN) return;
                    StudentsCharacterParameters s = (StudentsCharacterParameters) characterParamaters;
                    s.getStudents().add(SPColor.valueOf(((Node) e.getSource()).getId()));
                    ingressoYourPB.getChildren().forEach(n -> n.setEffect(getHighlightEffect()));

                    if (s.getStudents().size() == 2) {
                        setEffectToCanteenStudents(null);
                    }

                    ((Node) e.getSource()).setEffect(null);
                });

                mensa.add(Iv, i, color.ordinal());
            }

        }
    }

    /**
     * updates canteen professors
     *
     * @param PB    reference to playerboard
     * @param mensa canteen gridpane
     */
    private void updateProfessor(PlayerBoard PB, GridPane mensa) {
        List<SPColor> professors = PB.getCanteen().getProfessors();
        ImageView Iv;
        for (SPColor professor : professors) {
            Iv = new ImageView(GUIResourcesManager.getSingleton().getProfessorImage(professor));
            mensa.add(Iv, 11, professor.ordinal());
            Iv.setFitHeight(25.0);
            Iv.setFitWidth(25.0);
            DropShadow dropShadow = new DropShadow(8, Color.color(0.3, 0.3, 0.3));
            Iv.setEffect(dropShadow);

        }


    }

    /**
     * updates entrance students
     *
     * @param PB       reference to playerboard
     * @param ingresso entrance gridpane
     * @param students SPColor list containing the students in the entrance
     */
    private void updateIngresso(PlayerBoard PB, List<SPColor> students, GridPane ingresso) {

        ingresso.getChildren().clear();


        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 2 && i * 2 + j <= students.size(); j++) {

                if (i == 0 && j == 0) continue;

                ImageView studentIV = new ImageView(GUIResourcesManager.getSingleton().getStudentImage(students.get(i * 2 + j - 1)));
                studentIV.setFitWidth(25);
                studentIV.setFitHeight(25);
                studentIV.setPickOnBounds(true);

                studentIV.setId(students.get(i * 2 + j - 1).toString());
                studentIV.setOnMouseClicked((e) -> {

                    // gestisce il primo click sulla entrance caso joker
                    if (waitingInputState == WaitingInputState.CHARACTER_JOKER_CARD) {
                        getCharacterGridPane(CharacterEnum.JOKER).getChildren().forEach(c -> c.setEffect(null));
                        selectedStudents = ((StudentsCharacterParameters) characterParamaters).getStudents().size();
                        waitingInputState = WaitingInputState.CHARACTER_JOKER_ENTRANCE;

                        ((Node) e.getSource()).setEffect(null);
                    }

                    // gestisce il primo click sulla entrance caso musician
                    if (waitingInputState == WaitingInputState.CHARACTER_MUSICIAN_CANTEEN) {
                        setEffectToCanteenStudents(null);
                        selectedStudents = ((StudentsCharacterParameters) characterParamaters).getStudents().size();
                        waitingInputState = WaitingInputState.CHARACTER_MUSICIAN_ENTRANCE;
                        mensaYourPB.setEffect(null);
                        ingressoYourPB.getChildren().forEach(c -> c.setEffect(getHighlightEffect()));
                        ((Node) e.getSource()).setEffect(null);
                    }

                    // gestisce la move student del turno
                    if (waitingInputState == WaitingInputState.STUDENT_MOVE_STUDENT || waitingInputState == WaitingInputState.STUDENT_MOVE_DESTINATION) {

                        if (waitingInputState == WaitingInputState.STUDENT_MOVE_DESTINATION) { // cambiato idea
                            ingressoYourPB.getChildren().forEach(c -> c.setEffect(getHighlightEffect()));
                        } else {
                            waitingInputState = WaitingInputState.STUDENT_MOVE_DESTINATION;
                        }

                        selectedStudent = SPColor.valueOf(((Node) e.getSource()).getId());
                        ((Node) e.getSource()).setEffect(null);
                    }

                    // gestisce la chiusura del musician
                    if (List.of(WaitingInputState.CHARACTER_MUSICIAN_ENTRANCE, WaitingInputState.CHARACTER_JOKER_ENTRANCE).contains(waitingInputState)) {
                        StudentsCharacterParameters s = (StudentsCharacterParameters) characterParamaters;
                        s.getStudents().add(SPColor.valueOf(((Node) e.getSource()).getId()));
                        ((Node) e.getSource()).setEffect(null);
                        if (((StudentsCharacterParameters) characterParamaters).getStudents().size() == 2 * selectedStudents) {
                            ingressoYourPB.getChildren().forEach(c -> c.setEffect(null));
                            setCharacterButtonsVisibility(false);
                            gameManager.endRequestCharacter(characterType, characterParamaters, true);
                        }
                    }


                });

                ingresso.add(studentIV, j, i);
            }

        }


    }

    /**
     * updates tower view
     *
     * @param PB     reference to playerbiard
     * @param torri  tower gridpane
     * @param towers list containing the player's towers
     */
    private void updateTorri(PlayerBoard PB, List<TowerColor> towers, GridPane torri) {
        int count = 0;
        ImageView Iv;
        if (towers.size() == 0) return;

        torri.getChildren().clear();
        TowerColor towerColor = towers.get(0);
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 4 && count < towers.size(); j++, count++) {
                Iv = new ImageView(GUIResourcesManager.getSingleton().getTowerImage(towerColor));
                Iv.setFitHeight(25);
                Iv.setFitWidth(20);
                Iv.setPickOnBounds(true);
                torri.add(Iv, i, j);
            }

        }
    }

    /**
     * updates  displayed characters chards
     *
     * @param b reference to the  GameBoard
     * @param h gridpane containing caracter cards (implemented as buttons)
     */
    private void updateCharacterCards(Board b, GridPane h) {

        h.getChildren().removeIf(node -> GridPane.getRowIndex(node) == null || GridPane.getRowIndex(node) == 0);

        // per ogni character nella lista del player deve aggiungere un immagine all' HBOX
        List<CharacterCard> characters = b.getCharacterCards();
        int current = 0;

        for (CharacterCard character : characters) {

            Image characterImage = GUIResourcesManager.getSingleton().getCharacterImage(character.getEnumType());

            ImageView imageView = new ImageView(characterImage);


            //Insets margin = new Insets(0, 10, 0, current == 0 ? 50 : 10);

            //GridPane.setMargin(imageView, margin); // metti il margine sulla imageview

            // quanto dobbiamo scalare sulle x e sulle y (sulle X c'è da dividere per 3)
            double factorX = ((h.getWidth()) / 3) / characterImage.getWidth();
            double factorY = (h.getHeight() * 0.7) / characterImage.getHeight();

            double newSizeX = factorX * characterImage.getWidth();
            double newSizeY = factorY * characterImage.getHeight();

            imageView.setPreserveRatio(true);
            if (newSizeX < newSizeY)
                imageView.setFitWidth(newSizeX);
            else
                imageView.setFitHeight(newSizeY);

            imageView.setId("character_" + character.getEnumType().toString());
            imageView.setOnMouseClicked(this::characterClicked);

            Pane characterPane = new Pane();
            characterPane.setId(character.getEnumType().toString());
            h.add(characterPane, current++, 0);
            characterPane.getChildren().add(imageView);

            GridPane modifiersGP = new GridPane();
            modifiersGP.setHgap(5);
            modifiersGP.setVgap(5);

            modifiersGP.getRowConstraints().setAll(new RowConstraints(), new RowConstraints(), new RowConstraints(), new RowConstraints());
            modifiersGP.getColumnConstraints().setAll(new ColumnConstraints(), new ColumnConstraints(), new ColumnConstraints());
            modifiersGP.setStyle("-fx-padding: 20 20 20 20");
            modifiersGP.toFront();
            characterPane.getChildren().add(modifiersGP);

            if (character instanceof JokerCharacter jc) {
                addStudentCharacter(characterPane, jc.getStudents(), e -> {
                    if (waitingInputState != WaitingInputState.CHARACTER_JOKER_CARD) return;
                    ImageView source = (ImageView) e.getSource();
                    SPColor color = SPColor.valueOf(source.getId());
                    ingressoYourPB.getChildren().forEach(c -> c.setEffect(getHighlightEffect()));

                    ((StudentsCharacterParameters) characterParamaters).getStudents().add(color);
                    source.setEffect(null);

                    if (((StudentsCharacterParameters) characterParamaters).getStudents().size() == 3) {
                        lblStatus.setText("Select students in canteen to switch");
                        getCharacterGridPane(CharacterEnum.JOKER).getChildren().forEach(node -> node.setEffect(null));
                        selectedStudents = 3;
                        waitingInputState = WaitingInputState.CHARACTER_JOKER_ENTRANCE;
                    }
                });
            }

            if (character instanceof SommelierCharacter sc) {
                addStudentCharacter(characterPane, sc.getStudents(), null);
            }

            if (character instanceof LadyCharacter lc) {
                addStudentCharacter(characterPane, lc.getStudents(), null);
            }

            if (character instanceof HerbalistCharacter hc) {
                for (int i = 0; i < hc.getBans(); i++) {
                    ImageView banIV = new ImageView(GUIResourcesManager.getSingleton().getBanImage());
                    banIV.setFitWidth(25);
                    banIV.setFitHeight(25);
                    banIV.toFront();
                    addImageCharacter(characterPane, banIV);
                }
            }

            if (character.isUsed()) {
                ImageView coinIV = new ImageView(GUIResourcesManager.getSingleton().getCoinImage());
                coinIV.setFitWidth(25);
                coinIV.setFitHeight(25);
                addImageCharacter(characterPane, coinIV);
            }
        }
    }

    /**
     * puts students on character card
     *
     * @param p     pane containing the character
     * @param S     list of SPColor to put
     * @param event to assign to every student's image view
     */
    private void addStudentCharacter(Pane p, List<SPColor> S, EventHandler<MouseEvent> event) {
        for (int i = 0; i < S.size(); i++) {
            ImageView studentIV = new ImageView(GUIResourcesManager.getSingleton().getStudentImage(S.get(i)));
            studentIV.setFitHeight(25);
            studentIV.setFitWidth(25);
            studentIV.setPickOnBounds(true);
            studentIV.setOnMouseClicked(event);
            studentIV.setId(S.get(i).toString());
            addImageCharacter(p, studentIV);
        }
    }

    /**
     * adds card image to character pane
     *
     * @param characterPane pane containing character
     * @param imageview     imageview containing character image
     */
    private void addImageCharacter(Pane characterPane, ImageView imageview) {
        Node modifiersGP = characterPane.getChildren().stream().filter(gp -> gp instanceof GridPane).findFirst().orElse(null); // YOLO
        if (modifiersGP == null) throw new RuntimeException("No GP in this character");

        GridPane modifiersGP1 = (GridPane) modifiersGP;
        int modnum = (int) modifiersGP1.getChildren().stream().filter(c -> c instanceof ImageView).count();

        modifiersGP1.add(imageview, modnum % modifiersGP1.getColumnConstraints().size(), modnum / modifiersGP1.getColumnConstraints().size());
    }

    /**
     * finds gridpane associated to character
     *
     * @param character selected character
     * @return gridpane containing
     */
    private GridPane getCharacterGridPane(CharacterEnum character) {
        Node characterNode = yourCharacters.getChildren().stream().filter(p -> p.getId().equals(character.toString())).findFirst().orElse(null);

        if (!(characterNode instanceof Pane)) throw new RuntimeException("Character pane not found");

        Node gridpane = ((Pane) characterNode).getChildren().stream().filter(c -> c instanceof GridPane).findFirst().orElse(null);

        if (!(gridpane instanceof GridPane)) throw new RuntimeException("Character does not have a children grdpane");

        return (GridPane) gridpane;
    }

    @Override
    public void beginRequestLoginInfo(JoinResponseMessage.JoinReponseCode respCode) {

    }
    //endregion

    //region IView methods

    @Override
    public void beginRequestAssistantCard(boolean b) {
        Platform.runLater(() -> {
            lblStatus.setText("Select an assistant card to play");

            if (b) {
                Support.JFXAlertShowWait(Alert.AlertType.WARNING, "Invalid move", "You can't play this card");
            }

            int count = 0;
            AssistCard selectedCard = null;

            while (selectedCard == null) {
                AssistantSelectorController asc = AssistantSelectorController.createAndDisplayAndWait(gameManager.getMyPlayer().getHand(), "Select assistant card to play");
                selectedCard = asc.getSelectedCard();

                if (count++ > 2) System.exit(0);
            }

            gameManager.endRequestAssistCard(selectedCard);
        });
    }

    @Override
    public void beginRequestCharacter(String s) {
        Platform.runLater(() -> {
            lblStatus.setText("Select a character card to play or select \"NO\"");

            setCharacterButtonsVisibility(true);

            if (s != null && !s.equals("")) {
                Support.JFXAlertShowWait(Alert.AlertType.WARNING, "Invalid move", s);
            }

            yourCharacters.setEffect(new DropShadow(30, 0, 0, Color.WHITE));

            waitingInputState = WaitingInputState.CHARACTER_INPUT;
        });
    }

    @Override
    public void beginRequestStudentMove(EvaluateMoveMessage.MoveResponse code, String reason) {

        Platform.runLater(() -> {
            lblStatus.setText("Select a student to move and its destination");

            if (code != null && code != EvaluateMoveMessage.MoveResponse.MOVE_OK) {
                Support.JFXAlertShowWait(Alert.AlertType.WARNING, "Invalid move", reason);
            }

            this.waitingInputState = WaitingInputState.STUDENT_MOVE_STUDENT;

            ingressoYourPB.getChildren().forEach(c -> c.setEffect(getHighlightEffect()));
        });

    }

    @Override
    public void beginRequestMotherNatureMoves(boolean b) {

        Platform.runLater(() -> {
            lblStatus.setText("Select where to move Mother Nature");

            if (b) {
                Support.JFXAlertShowWait(Alert.AlertType.WARNING, "Invalid move", "You can't move mother nature here");
            }

            this.waitingInputState = WaitingInputState.MOTHER_NATURE_MOVES;

            setEffectToNextIslands(getHighlightEffect(), gameManager.getMoves());
        });

    }

    @Override
    public void beginRequestCloud(boolean b) {
        Platform.runLater(() -> {
            lblStatus.setText("Select a cloud");

            if (b) {
                Support.JFXAlertShowWait(Alert.AlertType.WARNING, "Invalid move", "You can't pick this cloud");
            }

            this.waitingInputState = WaitingInputState.CLOUD_NUMBER;

            Clouds.setEffect(getHighlightEffect());
        });
    }

    public void gridCloudClicked(MouseEvent event) {
        GridPane source = (GridPane) event.getSource();

        if (waitingInputState != WaitingInputState.CLOUD_NUMBER) return;

        waitingInputState = WaitingInputState.NONE;

        Clouds.setEffect(null);
        String s = source.getId().replace("gridCloud", "");
        int num = Integer.parseInt(s);
        gameManager.endRequestCloud(num);
    }

    @Override
    public void turnChanged(Player currentPlayerTurn) {
        Platform.runLater(() -> {

            if (currentPlayerTurn.getId() == gameManager.getMyPlayer().getId()) {
                java.awt.Toolkit.getDefaultToolkit().beep();
                yourName.setStyle("-fx-background-color: #f16a6a");
                oppName.setStyle(null);
                if (opp1Name != null)
                    opp1Name.setStyle(null);
            } else {
                lblStatus.setText("It's not your turn");
            }

            if (currentPlayerTurn.getId() == gameManager.getOpponentPlayers().get(0).getId()) {
                oppName.setStyle("-fx-background-color: #f16a6a");
                yourName.setStyle(null);
                if (opp1Name != null)
                    opp1Name.setStyle(null);
            }

            if (gameManager.getOpponentPlayers().size() == 2 &&
                    currentPlayerTurn.getId() == gameManager.getOpponentPlayers().get(1).getId()) {
                opp1Name.setStyle("-fx-background-color: #f16a6a");
                yourName.setStyle(null);
                oppName.setStyle(null);
            }


        });
    }

    @Override
    public void gameEnded(Player winner) {
        Platform.runLater(() -> {

            Alert winAlert = new Alert(Alert.AlertType.INFORMATION);
            winAlert.setTitle("Game ended");
            winAlert.setContentText("%s won the game".formatted(winner.getName()));
            if (!winner.getName().equals(gameManager.getMyPlayer().getName())) playMusic("/media/clown.mp3");
            if (winner.getName().equals(gameManager.getMyPlayer().getName())) playMusic("/media/jojo.mp3");
            gameManager.disconnect();

            winAlert.showAndWait();
            System.exit(0);
        });

    }

    @Override
    public void start() {

    }

    @Override
    public void notifyLoginOk() {

    }

    @Override
    public void exitGame(String message) {
        Platform.runLater(() -> {

            Alert winAlert = new Alert(Alert.AlertType.INFORMATION);
            winAlert.setTitle("Game ended");
            winAlert.setContentText(message);
            gameManager.disconnect();

            winAlert.showAndWait();
            System.exit(0);
        });
    }

    @Override
    public void notifyGameStarted() {

    }

    /**
     * reacts to click on a character
     *
     * @param event mouse event of the selected character
     */
    private void characterClicked(MouseEvent event) {
        CharacterEnum playedCharacterEnum = CharacterEnum.valueOf(((ImageView) event.getSource()).getId().split("_")[1]);
        CharacterCard character = gameManager.getModel().getGameBoard().getAvailableCharacterByType(playedCharacterEnum);

        if (waitingInputState != WaitingInputState.CHARACTER_INPUT) {
            try {
                CharacterColorDialogController.createAndDisplayAndWait(playedCharacterEnum.toString(), null, character);
            } catch (IOException ignored) {

            }
            return;
        }


        yourCharacters.setEffect(null);
        characterParamaters = null;
        characterType = playedCharacterEnum;
        SPColor selected;

        switch (playedCharacterEnum) {
            case SOMMELIER: // DA FARE; UNO STUDENTE DA QUESTA CARTA ED UN ISOLA DOVE METTERLO
                StudentIntCharacterParameters sommParams = new StudentIntCharacterParameters();

                try {
                    selected = CharacterColorDialogController.createAndDisplayAndWait("Please select a color <3", ((SommelierCharacter) character).getStudents().stream().distinct().toList(), character);
                } catch (IOException exc) {
                    return;
                }

                sommParams.setChosenStudent(selected);
                setEffectToIslands(getHighlightEffect());
                characterParamaters = sommParams;
                waitingInputState = WaitingInputState.CHARACTER_SOMMELIER_ISLAND;
                return;
            case HERBALIST: // TESTATI; ISOLA
            case MESSENGER:
                waitingInputState = WaitingInputState.CHARACTER_MESSENGER_ISLAND;
                setEffectToIslands(getHighlightEffect());
                return;
            case JOKER: // DA FARE; FINO A 3 STUDENTI DA QUI DA SCAMBIARE CON INGRESSO
                lblStatus.setText("Select students on the joker to switch");
                getCharacterGridPane(CharacterEnum.JOKER).getChildren().forEach(c -> c.setEffect(getHighlightEffect()));
                waitingInputState = WaitingInputState.CHARACTER_JOKER_CARD;
                characterParamaters = new StudentsCharacterParameters();
                selectedStudents = 0;
                return;
            case MERCHANT: // TESTATI
            case SINISTER:
                try {
                    selected = CharacterColorDialogController.createAndDisplayAndWait("Please select a color you dumbass", Arrays.stream(SPColor.values()).toList(), character);
                } catch (IOException exc) {
                    return;
                }
                characterParamaters = new StudentsCharacterParameters(List.of(selected));
                break;
            case MUSICIAN:

                characterParamaters = new StudentsCharacterParameters();
                waitingInputState = WaitingInputState.CHARACTER_MUSICIAN_CANTEEN;
                setEffectToCanteenStudents(getHighlightEffect());

                return;

            case LADY: // TESTATA

                try {
                    selected = CharacterColorDialogController.createAndDisplayAndWait("Please select a color", ((LadyCharacter) character).getStudents().stream().distinct().toList(), character);
                } catch (IOException exc) {
                    return;
                }
                characterParamaters = new StudentsCharacterParameters(List.of(selected));
                break;

            case CHEF:
            case POSTMAN:
            case CENTAUR:
            case KNIGHT:
                break;

        }
        setCharacterButtonsVisibility(false);
        gameManager.endRequestCharacter(playedCharacterEnum, characterParamaters, true);
    }
    //endregion

    //region flow_events

    /**
     * reacts to click on canteen
     */
    public void paneCanteenClicked() {
        if (waitingInputState != WaitingInputState.STUDENT_MOVE_DESTINATION) return;

        waitingInputState = WaitingInputState.NONE;
        ingressoYourPB.getChildren().forEach(c -> c.setEffect(null));
        gameManager.endRequestMoveStudent(selectedStudent, MoveStudentMessage.Destination.CANTEEN, -1);
    }

    /**
     * handles clicks on islands
     *
     * @param event mouse click on island
     */
    private void islandButtonClicked(MouseEvent event) {
        String buttonID = ((Button) event.getSource()).getId();
        int islandNumber = Integer.parseInt(buttonID.split("_")[1]);

        if (waitingInputState == WaitingInputState.STUDENT_MOVE_DESTINATION) {
            ingressoYourPB.getChildren().forEach(c -> c.setEffect(null));
            waitingInputState = WaitingInputState.NONE;
            gameManager.endRequestMoveStudent(selectedStudent, MoveStudentMessage.Destination.ISLAND, islandNumber);
            return;
        }

        if (waitingInputState == WaitingInputState.MOTHER_NATURE_MOVES) {
            // remove effect from islands
            setEffectToIslands(null);
            int islands = gameManager.getModel().getGameBoard().getIslands().size();
            waitingInputState = WaitingInputState.NONE;
            int moves = ((islandNumber - gameManager.getCurrentIslandIndex()) % islands);
            if (moves < 1 && islandNumber < gameManager.getCurrentIslandIndex())
                moves += gameManager.getModel().getGameBoard().getIslands().size();
            if (moves == 0 && islands <= gameManager.getMoves())
                moves = islands;
            gameManager.endRequestMotherNatureMoves(moves);
            return;
        }

        if (waitingInputState == WaitingInputState.CHARACTER_MESSENGER_ISLAND) {
            // remove effect from islands
            setEffectToIslands(null);
            waitingInputState = WaitingInputState.NONE;
            characterParamaters = new IntCharacterParameters(islandNumber);
            setCharacterButtonsVisibility(false);
            gameManager.endRequestCharacter(characterType, characterParamaters, true);
            return;
        }

        if (waitingInputState == WaitingInputState.CHARACTER_SOMMELIER_ISLAND) {
            // remove effect from islands
            setEffectToIslands(null);
            waitingInputState = WaitingInputState.NONE;
            ((StudentIntCharacterParameters) characterParamaters).setIslandNumber(islandNumber);
            setCharacterButtonsVisibility(false);
            gameManager.endRequestCharacter(characterType, characterParamaters, true);
            return;
        }

        // metodo non bloccante
        IslandController controller = IslandController.createAndDisplay(
                gameManager.getModel().getGameBoard().getIsland(islandNumber), islandNumber
        );


        // fai cose
    }

    /**
     * plays clown / JOJO music
     *
     * @param music song tiles
     */
    private void playMusic(String music) {
        try {
            MediaPlayer mp = new MediaPlayer(new Media(getClass().getResource(music).toString()));
            mp.play();
        } catch (Exception e) {
            System.out.println("Error playing music");
        }
    }
    //endregion

    //region helpers

    /**
     * creates highlight effect
     *
     * @return dropshadow
     */
    private Effect getHighlightEffect() {
        Glow ds = new Glow();
        ds.setLevel(0.7);
        return ds;
    }

    /**
     * set  NO and NOGFY to visibility like param
     *
     * @param visible true = visible, false = invisible
     */
    private void setCharacterButtonsVisibility(boolean visible) {
        btnCharacterNO.setVisible(visible);
        btnCharacterNOGFY.setVisible(visible);
    }

    /**
     * gets stage
     */
    private Stage getStage() {
        if (anchorPaneMain.getScene() == null) return null;
        return (Stage) anchorPaneMain.getScene().getWindow();
    }

    /**
     * disconnects
     */
    public void disconnect() {
        gameManager.disconnect();
    }

    private enum WaitingInputState {
        CHARACTER_INPUT,
        STUDENT_MOVE_STUDENT,
        STUDENT_MOVE_DESTINATION,
        MOTHER_NATURE_MOVES,
        CLOUD_NUMBER,
        CHARACTER_MESSENGER_ISLAND,
        CHARACTER_MUSICIAN_CANTEEN,
        CHARACTER_MUSICIAN_ENTRANCE,
        CHARACTER_SOMMELIER_ISLAND,
        CHARACTER_JOKER_ENTRANCE,
        CHARACTER_JOKER_CARD,
        NONE
    }
    //endregion helpers
}
