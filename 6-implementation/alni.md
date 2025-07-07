# Alni Riccardo
During the development of the project, I was primarily responsible for managing the model component of the game engine. 
I developed the core classes (*PlayerModel*, *DeckModel*, *CardModel*) and implemented the game logic handling within *EngineModel* and *GameContext*. 
I also worked on the implementation of the *HandRule* strategy.

## EngineModel
- logica di gioco in engine model
- implementazione full con deckManagement per isolare logiche di deck
- gestione delle regole e stato del gioco separata in gameContext

![EngineModel_Implementation](../res/implementation_model.png "EngineModel Implementation")

## Rule Strategies
- 4 tipi di rules come strategy
- di base implementazione di default (regole come briscola)
- regole custom settate dall'utente tramite engineModel

![Rules_Implementation](../res/implementation_rules.png "Rules Implementation")


## Hand Rules
- lambda (List[CardModel], DeckModel, CardModel) => Boolean
- settabili dall'utente -- 1 sola come combinazione di più regole con and/or
- alcune regole preimpostate (freeStart...)
- esempi
