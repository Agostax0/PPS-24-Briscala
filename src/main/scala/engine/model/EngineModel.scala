package engine.model

import dsl.types.Suits

trait EngineModel:
  def addPlayers(players: List[PlayerModel]): Unit
  def giveCardsToPlayers(handSize: Int): Unit

trait DeckManagement:
  import DeckModel.given
  var deck: DeckModel = DeckModel()

  def createDeck(suits: Suits, ranks: List[String]): Unit =
    for
      rank <- ranks
      suit <- suits
    do deck.addCard(CardModel(rank, ranks.indexOf(rank), suit))
    deck.shuffle()

  def shuffle(): Unit =
    deck.shuffle()

class FullEngineModel(val gameName: String)
    extends EngineModel
    with DeckManagement:
  var players: List[PlayerModel] = List.empty

  override def addPlayers(players: List[PlayerModel]): Unit =
    this.players = players

  override def giveCardsToPlayers(handSize: Int): Unit =
    this.players.foreach(player => player.drawFromDeck(deck, handSize))
