# Peer-Review 1: UML

Daniele Gazzola, Edoardo Fullin, Giacomo Groppi

Gruppo 39

Valutazione del diagramma UML delle classi del gruppo 38.

## Lati positivi

- la enum PlayerState sembra essere molto comoda per la gestione
  temporale del turno.
- la funzione applyEffect nella CharacterCard è un'idea che se implementata a dovere
  può risultare molto utile per aumentare la flessibilità dell' architettura
- implementare il Game come un singleton può semplificare l' architettura
  nel caso in cui non ci siano partite in parallelo.
- classi non monolitiche e struttura flessibile


## Lati negativi

#### Game:
  - playersNumber è ridondante (players.length())

#### GameBoard:
- GameBoard non dovrebbe essere singleton (è già attributo di un singleton)
- numIslands ridondante (islands.length())
- joinIslands e chooseCharacter non prendono parametri per 
decidere che oggetti modificare, inoltre non sembra esserci modo per tenere traccia di quali
isole sono state unite
- in professorController il parametro numberOfStudents può essere calcolato nel metodo
- calculateNewPositionMotherNature dovrebbe ricevere un int come parametro (numero mosse)

#### CharacterCard:

- increasePrice potrebbe restituire void
- initializeCard deve essere implementata da tutte le classi figlie
- applyEffect può agire solo sul player sebbene necessiti di agire anche
sul resto del Game

#### Island:
- towers viene dichiarato come un array di lunghezza 1
- getPlayerTower dovrebbe restituire un TowerColor rappresentativo del player che controlla l'isola

#### Student:
- getColor dovrebbe ritornare un StudentColor

#### Bag:
- Bag potrebbe non essere singleton (è già attributo singolo di un attributo singolo di un singleton)
quindi è già garantito unico
- getStudent e moveStudent assolvono alla stessa funzione logica: rimuovere uno studente
dalla bag implica per forza spostarlo da qualche parte.

#### Player:
- un player dovrebbe avere una plancia (la sua?) ma non una lista di plance (quelle degli altri?)
- moveMotherNature non serve nel player in quanto non dipende da che player sta giocando
- la funzione svolta da conquerIsland non dovrebbe logicamente essere svolta dal player,
  forse sarebbe meglio implementarla nella gameBoard.
- chooseAssistant dovrebbe avere come parametro l' assistente scelto.

#### AssistantCard:
- manca un attributo used nell'oggetto (o similare), non vediamo come si potrebbe tracciare altrimenti
se è stato usato oppure no
- la funziona isUsed non ha bisogno di parametri

#### AssistantCardsDeck:
- chooseNumberOfMoves non dovrebbe essere qua e non ha riferimenti a nulla
su cui agire, come anche playCard

#### Entrance:
- le funzioni moveStudentToStudentRow e moveStudentsToIsland dovrebbero ricevere anche uno studente ed
essere in un punto che abbia accesso sia all'oggetto di origine che di destinazione

#### StudentRow:
- giveCoin dovrebbe avere un riferimento al player
- rowColor è ridondante, è già contenuto nel color degli studenti nell'array students
(tutti gli studenti in una row devono avere lo stesso colore)

#### Tower:
- moveTower dovrebbe essere in un punto che abbia accesso sia all' oggetto di origine che di destinazione
(in modo da rimuovere l'oggetto dal punto di origine e inserirlo nella destinazione)


## Confronto tra le architetture

In linea di massima i due diagrammi si assomigliano, tuttavia potrebbe essere una buona
scelta implementare nel nostro progetto l'enum PlayerState per tenere traccia dello stato del player.
Nella plank/canteen abbiamo usato due implementazioni diverse per gestire gli studenti,
nel nostro caso le "studentRow" vengono calcolate a Runtime direttamente nella view
tenendo conto del colore degli studenti.
Infine, per quanto riguarda la gestione dei Characters la abbiamo gestita in un modo simile (con funzione applyEffect ereditata)
però la funzione ha bisogno di un riferimento a tutto il game in quanto potrebbe modificare lo stato di oggetti
a cui il player non ha riferimenti; per gli effetti non istantanei un'idea potrebbe essere di tenersi una lista nella board (oppure nel game)
che memorizzi un riferimento alle carte con gli effetti attualmente attivi e, durante la gestione della parte di gioco nella quale bisogna tenere conto degli effetti attivati, si verifica quali effetti sono attivi 
e nel caso li si applicano.