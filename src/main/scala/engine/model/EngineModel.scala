package engine.model

import dsl.types.Suits

trait EngineModel:
  var players: List[PlayerModel] = List.empty
  def addPlayers(players: List[PlayerModel]): Unit
  def playCard(player: PlayerModel, card: CardModel): Boolean

trait RuleManagement:
  def canPlayCard(card: CardModel): Boolean = true

trait TableManagement:
  var cardsOnTable: Map[PlayerModel, CardModel] = Map.empty
  def addCardToTable(player: PlayerModel, card: CardModel): Unit =
    cardsOnTable = cardsOnTable + (player -> card)
  def clearTable(): Unit = cardsOnTable = Map.empty
  def computeTurn(): Unit =
    val maxCard = cardsOnTable.values.max(Ordering.by(_.rank))
    val winningPlayer = cardsOnTable.find(_._2 == maxCard).map(_._1).get
    winningPlayer.increaseScore(1)


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

  override def playCard(player: PlayerModel, card: CardModel): Boolean =
    if player.eq(activePlayer) && canPlayCard(card) then
      player.playCard(card)
      addCardToTable(player, card)
      nextPlayerTurn()
      true
    else false

  override def addPlayers(players: List[PlayerModel]): Unit =
    this.players = players
    setStartingPlayer(0)

  private def setStartingPlayer(index: Int): Unit =
    activePlayer = players(index)

  private def nextPlayerTurn(): Unit =
    val playerIndex = (players.indexOf(activePlayer) + 1) % players.size
    activePlayer = players(playerIndex)
