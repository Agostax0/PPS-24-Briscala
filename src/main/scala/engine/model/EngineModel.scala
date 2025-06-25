package engine.model

import dsl.types.Suits

trait DeckManagement:
  var deck: DeckModel = DeckModel()
  
  def createDeck(suits: Suits, ranks: List[String]): Unit =
    for rank <- ranks 
      suit <- suits do
        deck.addCard(CardModel(rank, ranks.indexOf(rank), suit))

class EngineModel(val gameName: String) extends DeckManagement:
  var players: List[PlayerModel] = List.empty
  
  def addPlayers(players: List[PlayerModel]): Unit =
    this.players = players

  def giveCardsToPlayers(handSize: Int): Unit =
    this.players.foreach(
        player => player.drawFromDeck(deck, handSize)
    )
