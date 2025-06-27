package engine.model

import dsl.types.Suits

trait EngineModel:
  var players: List[PlayerModel] = List.empty
  def addPlayers(players: List[PlayerModel]): Unit
  def playCard(player: PlayerModel, card: CardModel): Unit

trait RuleManagement:
  def canPlayCard(card: CardModel): Boolean = true

trait TableManagement:
  var cardsOnTable: List[CardModel] = List.empty
  def addCardToTable(card: CardModel): Unit =
    cardsOnTable = cardsOnTable :+ card
  def clearTable(): Unit = cardsOnTable = List.empty

trait DeckManagement:
  engineModel: EngineModel =>
  import DeckModel.given
  var deck: DeckModel = DeckModel()
  def giveCardsToPlayers(handSize: Int): Unit =
    engineModel.players.foreach(player => player.drawFromDeck(deck, handSize))

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
    with DeckManagement
    with RuleManagement
    with TableManagement:
  private var activePlayer: PlayerModel = _

  override def playCard(player: PlayerModel, card: CardModel): Unit =
    if player.eq(activePlayer) then
      player.playCard(card)
      addCardToTable(card)
      nextPlayerTurn()

  override def addPlayers(players: List[PlayerModel]): Unit =
    this.players = players
    setStartingPlayer(0)

  private def setStartingPlayer(index: Int): Unit =
    activePlayer = players(index)

  private def nextPlayerTurn(): Unit =
    val playerIndex = (players.indexOf(activePlayer) + 1) % players.size
    activePlayer = players(playerIndex)
