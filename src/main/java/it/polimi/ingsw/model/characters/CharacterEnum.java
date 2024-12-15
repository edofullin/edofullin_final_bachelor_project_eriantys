package it.polimi.ingsw.model.characters;

import org.jetbrains.annotations.Contract;

public enum CharacterEnum {
    CENTAUR,
    CHEF,
    HERBALIST,
    JOKER,
    KNIGHT,
    LADY,
    MERCHANT,
    MESSENGER,
    MUSICIAN,
    POSTMAN,
    SINISTER,
    SOMMELIER;

    @Contract(pure = true)
    public String getDescription() {
        return switch (this) {
            case CENTAUR -> "When resolving a conquering on an island, " +
                    "towers do not count towards influence.";
            case CHEF -> "During this turn, you take control of any " +
                    "number of professors even if you have the same " +
                    "number of students as the player who currently " +
                    "controls them.";
            case HERBALIST -> "Place a herbalist ban tile on an island of your choice. " +
                    "The first time Mother Nature ends her movement " +
                    "there, put the herbalist ban back onto this card\nDO NOT " +
                    "calculate influence on that island, or place any towers.";
            case JOKER -> "You may take up to 3 students from this card " +
                    "and replace them with the same number of students " +
                    "from your entrance.";
            case KNIGHT -> "During the influence calculation this turn, you " +
                    "count as having 2 more influence.";
            case LADY -> "Take 1 student from this card and place it in " +
                    "your dining Room. Then, draw a new Student from the " +
                    "bag and place it on this card.";
            case MERCHANT -> "Choose a color of student: during the influence " +
                    "calculation this turn, that color adds no influence.";
            case MESSENGER -> "Choose an island and resolve the island as if " +
                    "Mother Nature had ended her movement there. Mother " +
                    "Nature will still move and the Island where she ends " +
                    "her movement will also be resolved.";
            case MUSICIAN -> "You may exchange up to 2 students between " +
                    " your entrance and your dining room.";
            case POSTMAN -> "You may move Mother Nature up to 2 " +
                    "additional islands than is indicated by the assistant " +
                    "card you've played.";
            case SINISTER -> "Choose a type of student: every player " +
                    "(including yourself) must return 3 students of that type " +
                    "from their dining room to the bag. If any player has " +
                    "fewer than 3 students of that type, return as many " +
                    "students as they have.";
            case SOMMELIER -> "Take 1 student from this card and place it on " +
                    "an island of your choice. Then, draw a new Student " +
                    "from the bag and place it on this card.";
        };
    }

}
