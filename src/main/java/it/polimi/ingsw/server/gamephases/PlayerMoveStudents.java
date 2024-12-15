package it.polimi.ingsw.server.gamephases;

import it.polimi.ingsw.model.PlayerBoard;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.server.Client;
import it.polimi.ingsw.server.Configuration;
import it.polimi.ingsw.server.IConnectionManager;
import it.polimi.ingsw.server.MatchController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerMoveStudents implements IGamePhase {

    final private static Logger logger = LogManager.getLogger(PlayerMoveStudents.class);
    private final Client currentClient;
    private int studentsMoved;

    public PlayerMoveStudents(int studentsMoved, Client currClient) {
        this.studentsMoved = studentsMoved;
        this.currentClient = currClient;
    }

    @Override
    public void preAction(MatchController matchController, IConnectionManager connectionManager) {
        // informs all clients who plays now
        matchController.getGameModel().setCurrentTurn(currentClient.getGamePlayer());

        if (Configuration.getConfiguration().isEnableDiskSave() && matchController.getGameSettings().getSaveOnDisk()) {
            try {
                matchController.saveGame();
            } catch (Exception ex) {
                logger.error("Could not save current game", ex);
            }
        }

        currentClient.sendMessageAsync(new ModelUpdateMessage(matchController.getStrippedGameModel()));
        connectionManager.broadcastMessage(new TurnChangedMessage(currentClient.getId()));
    }

    @Override
    public void messageReceived(MatchController matchController, Client client, Message message) {

        if (!client.equals(currentClient)) {
            client.sendMessageAsync(new EvaluateMoveMessage(EvaluateMoveMessage.MoveResponse.MOVE_KO, matchController.getGameModel()));
            return; // non dovevi giocare tu pirla
        }

        PlayerBoard currBoard = matchController.getGameModel().getCurrentPlayerBoard();
        int studentsToMove = matchController.getQueue().size() == 3 ? 4 : 3;

        if (message instanceof MoveStudentMessage psMessage) {

            if (currBoard.getStudents().contains(psMessage.getStudent())) {
                // studente giusto

                logger.debug(String.format("player %d moved %s to %s", client.getId(), psMessage.getStudent().toString(), psMessage.getDestination().toString()));

                if (psMessage.getDestination() == MoveStudentMessage.Destination.CANTEEN
                        && currBoard.getCanteen().getStudents().stream()
                        .filter(st -> st == psMessage.getStudent())
                        .count() < 10) {
                    currBoard.getCanteen().addStudent(psMessage.getStudent());
                    currBoard.removeStudent(psMessage.getStudent());
                    long students = currBoard.getCanteen().getStudents().stream().filter(s -> s == psMessage.getStudent()).count();
                    if ((students == 3 || students == 6 || students == 9) && matchController.getGameModel().getGameBoard().getCoins() > 0)
                        currBoard.getOwner().modifyCoins(1);
                    matchController.getGameModel().getGameBoard().updateProfessors();
                    client.sendMessageAsync(new EvaluateMoveMessage(message.getMessageId(), EvaluateMoveMessage.MoveResponse.MOVE_OK, "ok", matchController.getStrippedGameModel()));
                    studentsMoved++;
                } else if (psMessage.getDestination() == MoveStudentMessage.Destination.ISLAND) {
                    matchController.getGameModel().getGameBoard().getIslands().get(psMessage.getNIsland()).addStudent(psMessage.getStudent());
                    currBoard.removeStudent(psMessage.getStudent());
                    client.sendMessageAsync(new EvaluateMoveMessage(message.getMessageId(), EvaluateMoveMessage.MoveResponse.MOVE_OK, "ok", matchController.getStrippedGameModel()));
                    studentsMoved++;
                } else {
                    client.sendMessageAsync(new EvaluateMoveMessage(message.getMessageId(), EvaluateMoveMessage.MoveResponse.MOVE_KO, "Your canteen is full", matchController.getStrippedGameModel()));
                }

                if (studentsMoved == studentsToMove) {
                    logger.debug("students moved, updating professors");
                    matchController.releasePhase();
                }
            } else {
                // risponde con un check-ko
                client.sendMessageAsync(new EvaluateMoveMessage(message.getMessageId(), EvaluateMoveMessage.MoveResponse.MOVE_KO, "you dont have the student you want to move", matchController.getStrippedGameModel()));
            }
        }
    }

    @Override
    public void postAction(MatchController matchController, IConnectionManager connectionManager) {
        // informo tutti tranne current del nuovo model (eccetto current che lo ha già ricevuto nella evaluatemove)
        connectionManager.broadcastMessageExcept(currentClient, new ModelUpdateMessage(matchController.getStrippedGameModel()));
    }

    @Override
    public IGamePhase nextPhase(MatchController matchController) {
        return new PlayerMoveMotherNature(currentClient);
    }
}
