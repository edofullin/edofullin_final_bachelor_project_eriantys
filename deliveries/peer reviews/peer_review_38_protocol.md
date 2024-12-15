# Peer-Review 2: Protocollo di rete

Daniele Gazzola, Edoardo Fullin, Giacomo Groppi

Gruppo 39

Valutazione del protocollo di rete del gruppo 38.

## Lati positivi

- Il protocollo gestisce le partite multiple correttamente
- Non inviando il model completo ma le singole azioni ogni volta si risparmia banda

## Lati negativi

- CreateMatchRequest ha bisogno di un parametro per scegliere se si vuole giocare in modalità esperto.
- La costante playerState è ridondante nei vari messaggi per le fasi di gioco. 
Si potrebbe fare un unico messaggio contenente un valore dell'eunum oppure N messaggi senza il valore dell'enum.
- I client non hanno le mosse che effettuano gli altri giocatori, quindi non è possibile determinare lo stato attuale del gioco
- Mancano i messaggi per gestire le disconnessioni (volute) dei vari giocatori, come anche un messaggio di keep-alive per quelle non volute.
- Si potrebbe inviare il messaggio StartPianification in broadcast per informate anche gli altri giocatori di chi deve giocare.
- I messaggi "connect" e "lobbies" presenti nello scenario #1 non sono presenti nella descrizione.
- Mancano tutti gli scenari dopo la fase di Pianification

## Confronto tra le architetture

Il protocollo del gruppo 38 differisce dal nostro riguardo in diversi
aspetti, tra cui modalità di aggiornamento del Model. 
Noi abbiamo deciso di inviare quando necessario l’intero nuovo stato
della Board, al contrario loro hanno scelto di aggiornare solo le parti di
board interessate dal cambiamento, questo comporta un risparmio di traffico sulla rete
ma richiede di gestire un numero di messaggi molto elevato rispetto alla semplice sostituzione del model.



