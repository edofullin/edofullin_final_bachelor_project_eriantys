package it.polimi.ingsw.model.characters;

public class FactoryCharacterCard {

    public CharacterCard createCharacter(CharacterEnum character) {

        return switch (character) {
            case CENTAUR -> new CentaurCharacter();
            case CHEF -> new ChefCharacter();
            case HERBALIST -> new HerbalistCharacter();
            case JOKER -> new JokerCharacter();
            case KNIGHT -> new KnightCharacter();
            case LADY -> new LadyCharacter();
            case MERCHANT -> new MerchantCharacter();
            case MESSENGER -> new MessengerCharacter();
            case MUSICIAN -> new MusicianCharacter();
            case POSTMAN -> new PostmanCharacter();
            case SINISTER -> new SinisterCharacter();
            case SOMMELIER -> new SommelierCharacter();
        };
    }
}
