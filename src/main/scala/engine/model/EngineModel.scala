package engine.model

import dsl.types.*
import dsl.types.HandRule.HandRule
import dsl.types.PlayRule.PlayRule
import dsl.types.PointsRule.PointsRule
import dsl.types.Team.Team
import dsl.types.WinRule.WinRule

import scala.collection.immutable.List

class GameContext:
  private var handRuleStrategy: HandRuleStrategy = DefaultHandRuleStrategy()
  private var playRuleStrategy: PlayRuleStrategy = DefaultPlayRuleStrategy()
  private var pointsStrategy: PointsStrategy = DefaultPointsStrategy()
  private var winRuleStrategy: WinRuleStrategy = DefaultWinRuleStrategy()

  var cardsOnTable: List[(PlayerModel, CardModel)] = List.empty
  var briscolaSuit: String = ""

  def setHandRule(rule: HandRule): Unit =
    handRuleStrategy = CustomHandRuleStrategy(rule)

  def setPlayRules(rules: List[PlayRule]): Unit =
    playRuleStrategy = CustomPlayRuleStrategy(rules)

  def setPointRules(rules: List[PointsRule]): Unit =
    pointsStrategy = CustomPointsStrategy(rules)

  def setWinRule(rule: WinRule): Unit =
    winRuleStrategy = CustomWinRuleStrategy(rule)

  def setBriscolaSuit(suit: String): Unit =
    this.briscolaSuit = suit

  def addCardToTable(player: PlayerModel, card: CardModel): Unit =
    cardsOnTable = cardsOnTable :+ (player, card)

  def clearTable(): Unit =
    cardsOnTable = List.empty

  def canPlayCard(playerHand: DeckModel, playedCard: CardModel): Boolean =
    handRuleStrategy.canPlayCard(cardsOnTable.map(_._2), playerHand, playedCard)

  def calculateTurn(
      table: List[(PlayerModel, CardModel)] = cardsOnTable
  ): Option[PlayerModel] =
    playRuleStrategy.calculateWinningPlayer(table)

  def calculatePoints(): Int =
    pointsStrategy.calculatePoints(cardsOnTable.map((player, card) => card))

  def calculateWinner(teams: List[Team], players: List[PlayerModel]): Team =
    winRuleStrategy.winningGameTeam(teams, players)

trait EngineModel:
  var players: List[PlayerModel] = List.empty
  var teams: List[Team] = List.empty
  var activePlayer: PlayerModel = _

  def addPlayers(players: List[PlayerModel]): Unit

  def addTeams(teams: List[Team]): Unit

  def computeTurn(): Unit

  def setStartingPlayer(index: Int): Unit

  def playCard(player: PlayerModel, card: CardModel): Boolean

  def botPlayCard(bot: BotPlayerModel): CardModel

  def winningGamePlayers(): Team

class FullEngineModel(
    val gameName: String,
    private val context: GameContext = GameContext()
) extends EngineModel
    with DeckManagement:

  override def addPlayers(players: List[PlayerModel]): Unit =
    this.players = players
    setStartingPlayer(0)

  override def addTeams(teams: List[Team]): Unit =
    if teams.isEmpty then
      this.teams = players.map(player => List(player.name).asInstanceOf[Team])
    else
      val existingPlayers = teams.flatMap(Team.toList).toSet
      val missingPlayers =
        players.map(p => p.name).filterNot(existingPlayers.contains)
      val missingTeams =
        missingPlayers.map(player => List(player).asInstanceOf[Team])
      this.teams = teams ++ missingTeams

    if this.players.size == 4 && this.teams.size == 2 && this.teams.head.size == 2 then
      val playersOrder = List(
        this.teams.head(0),
        this.teams(1)(0),
        this.teams.head(1),
        this.teams(1)(1)
      )
      this.players = players.sortBy(player => playersOrder.indexOf(player.name))

  override def setStartingPlayer(index: Int): Unit =
    activePlayer = players(index)

  override def playCard(player: PlayerModel, card: CardModel): Boolean =
    if player.eq(activePlayer) && context.canPlayCard(player.hand, card) then
      player.playCard(card)
      context.addCardToTable(player, card)
      nextPlayerTurn()
      true
    else false

  override def botPlayCard(bot: BotPlayerModel): CardModel =
    bot.generateCard(context)

  override def computeTurn(): Unit =
    context.calculateTurn() match
      case Some(winningPlayer) =>
        addScoreToWinningPlayer(winningPlayer)
        setStartingPlayer(players.indexOf(winningPlayer))
        context.clearTable()
      case None =>
        throw new IllegalStateException("No winning player found for the turn")

  override def winningGamePlayers(): Team =
    context.calculateWinner(this.teams, this.players)

  private def addScoreToWinningPlayer(winningPlayer: PlayerModel): Unit =
    val points = context.calculatePoints()
    winningPlayer.increaseScore(points)

  private def nextPlayerTurn(): Unit =
    val playerIndex = (players.indexOf(activePlayer) + 1) % players.size
    activePlayer = players(playerIndex)

  def setHandRule(rule: HandRule): Unit = context.setHandRule(rule)
  def setPlayRules(rules: List[PlayRule]): Unit = context.setPlayRules(rules)
  def setPointRules(rules: List[PointsRule]): Unit =
    context.setPointRules(rules)
  def setWinRule(rule: WinRule): Unit = context.setWinRule(rule)
  def setBriscolaSuit(suit: String): Unit = context.setBriscolaSuit(suit)
  def giveCardsToPlayers(handSize: Int): Unit =
    players.foreach(player => giveCardsToPlayer(player, handSize))
