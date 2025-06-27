package engine.model

import dsl.types.{PointsRule, Suits}

trait EngineModel:
  var players: List[PlayerModel] = List.empty
  var activePlayer: PlayerModel = _
  def addPlayers(players: List[PlayerModel]): Unit
  def computeTurn(): Unit
  def setStartingPlayer(index: Int): Unit
  def playCard(player: PlayerModel, card: CardModel): Boolean

trait RuleManagement:
  def canPlayCard(card: CardModel): Boolean = true

trait TableManagement:
  var cardsOnTable: List[(PlayerModel, CardModel)] = List.empty
  var pointRules: List[PointsRule] = List.empty

  def addCardToTable(player: PlayerModel, card: CardModel): Unit =
    cardsOnTable = cardsOnTable :+ (player, card)

  def clearTable(): Unit = cardsOnTable = List.empty

  def calculateWinningPlayer(): PlayerModel =
    val firstCardPlayed = cardsOnTable.head._2
    val playedSuit = firstCardPlayed.suit
    val maxCardByPlayer =
      cardsOnTable.filter(_._2.suit == playedSuit).max(Ordering.by(_._2.rank))
    val winningPlayer = cardsOnTable.find(_.eq(maxCardByPlayer)).get._1
    addScoreToWinningPlayer(winningPlayer)
    winningPlayer

  private def addScoreToWinningPlayer(winningPlayer: PlayerModel): Unit =
    val points = (for
      card <- cardsOnTable.map(_._2)
      rule <- pointRules
    yield rule(card.name, card.suit)).sum
    winningPlayer.increaseScore(points)
    clearTable()

  def setPointRules(rules: List[PointsRule]): Unit = this.pointRules = rules

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

  override def playCard(player: PlayerModel, card: CardModel): Boolean =
    if player.eq(activePlayer) && canPlayCard(card) then
      player.playCard(card)
      addCardToTable(player, card)
      nextPlayerTurn()
      true
    else false

  override def computeTurn(): Unit =
    val winningPlayer = calculateWinningPlayer()
    setStartingPlayer(players.indexOf(winningPlayer))

  override def addPlayers(players: List[PlayerModel]): Unit =
    this.players = players
    setStartingPlayer(0)

  override def setStartingPlayer(index: Int): Unit =
    activePlayer = players(index)

  private def nextPlayerTurn(): Unit =
    val playerIndex = (players.indexOf(activePlayer) + 1) % players.size
    activePlayer = players(playerIndex)
