package engine.model

import dsl.types.{HandRule, PlayRule, PointsRule, Suits}
import dsl.types.{HandRule, PointsRule, Suits, Team}

trait EngineModel:
  var players: List[PlayerModel] = List.empty
  var teams: List[Team] = List.empty
  var activePlayer: PlayerModel = _
  def addPlayers(players: List[PlayerModel]): Unit
  def addTeams(teams: List[Team]): Unit
  def computeTurn(): Unit
  def setStartingPlayer(index: Int): Unit
  def playCard(player: PlayerModel, card: CardModel): Boolean
  def winningGamePlayers(): List[PlayerModel]

trait HandRuleManagement:
  table: TableManagement =>
  var handRule: Option[HandRule] = None

  def setHandRules(rule: HandRule): Unit = this.handRule = Some(rule)

  def canPlayCard(playerHand: DeckModel, playedCard: CardModel): Boolean =
    handRule match
      case Some(rule) =>
        rule(table.cardsOnTable.map(_._2), playerHand, playedCard)
      case None => true

trait PlayRuleManagement:
  table: TableManagement =>
  var playRules: List[PlayRule] = List.empty

  def setPlayRules(rules: List[PlayRule]): Unit = this.playRules = rules

  def calculateWinningPlayer(): PlayerModel =
    val winningPlayers: List[PlayerModel] = playRules.map(rule => rule(cardsOnTable)).filter(_.isDefined).map(_.get)

    if (winningPlayers.size > 1) then
      throw new IllegalStateException("More than one winning player")

    val winningPlayer = winningPlayers.head
    addScoreToWinningPlayer(winningPlayer)
    winningPlayer

  private def addScoreToWinningPlayer(winningPlayer: PlayerModel): Unit =
    val points = (for
      card <- cardsOnTable.map(_._2)
      rule <- pointRules
    yield rule(card.name, card.suit)).sum
    winningPlayer.increaseScore(points)
    clearTable()

trait TableManagement extends HandRuleManagement with PlayRuleManagement:
  var cardsOnTable: List[(PlayerModel, CardModel)] = List.empty
  var pointRules: List[PointsRule] = List.empty
  var briscolaSuit: String = ""

  def addCardToTable(player: PlayerModel, card: CardModel): Unit =
    cardsOnTable = cardsOnTable :+ (player, card)

  def setPointRules(rules: List[PointsRule]): Unit = this.pointRules = rules

  def setBriscolaSuit(suit: String): Unit = this.briscolaSuit = suit

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
    with TableManagement:

  override def playCard(player: PlayerModel, card: CardModel): Boolean =
    if player.eq(activePlayer) && canPlayCard(player.hand, card) then
      player.playCard(card)
      addCardToTable(player, card)
      nextPlayerTurn()
      true
    else false

  override def winningGamePlayers(): List[PlayerModel] =
    val winningPlayers = players.sortWith(_.score > _.score)
    winningPlayers

  override def computeTurn(): Unit =
    val winningPlayer = calculateWinningPlayer()
    setStartingPlayer(players.indexOf(winningPlayer))

  override def addPlayers(players: List[PlayerModel]): Unit =
    this.players = players
    setStartingPlayer(0)

  override def addTeams(teams: List[Team]): Unit =
    if teams.isEmpty then {
      this.teams = players.map(player => List(player.name).asInstanceOf[Team])
      println(this.teams)
    } else
      val existingPlayers = teams.flatMap(Team.toList).toSet
      val missingPlayers = players.map(p=>p.name).filterNot(existingPlayers.contains)
      val missingTeams = missingPlayers.map(player => List(player).asInstanceOf[Team])
      this.teams = teams ++ missingTeams
      println(this.teams)

    if this.players.size == 4 && this.teams.size == 2 && this.teams.head.size == 2 then {
      val playersOrder = List(this.teams.head(0), this.teams(1)(0), this.teams.head(1), this.teams(1)(1))
      this.players = players.sortBy(player => playersOrder.indexOf(player.name))
      println(this.players)
    }

  override def setStartingPlayer(index: Int): Unit =
    activePlayer = players(index)

  private def nextPlayerTurn(): Unit =
    val playerIndex = (players.indexOf(activePlayer) + 1) % players.size
    activePlayer = players(playerIndex)
