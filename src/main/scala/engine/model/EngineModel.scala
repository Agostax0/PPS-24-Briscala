package engine.model

import dsl.types.*

trait EngineModel:
  var players: List[PlayerModel] = List.empty
  var teams: List[Team] = List.empty
  var activePlayer: PlayerModel = _
  var winRule: WinRule = _
  def addPlayers(players: List[PlayerModel]): Unit
  def addTeams(teams: List[Team]): Unit
  def computeTurn(): Unit
  def setStartingPlayer(index: Int): Unit
  def playCard(player: PlayerModel, card: CardModel): Boolean
  def winningGamePlayers(): Team
  def setWinRule(rule: WinRule): Unit

class GameContext:
  private var handRuleStrategy: HandRuleStrategy = DefaultHandRuleStrategy()
  private var playRuleStrategy: PlayRuleStrategy = DefaultPlayRuleStrategy()
  private var pointsStrategy: PointsStrategy = DefaultPointsStrategy()

  var cardsOnTable: List[(PlayerModel, CardModel)] = List.empty
  var deck: DeckModel = DeckModel()
  var briscolaSuit: String = ""

  def setHandRule(rule: HandRule): Unit =
    handRuleStrategy = CustomHandRuleStrategy(rule)

  def setPlayRules(rules: List[PlayRule]): Unit =
    playRuleStrategy = CustomPlayRuleStrategy(rules)

  def setPointRules(rules: List[PointsRule]): Unit =
    pointsStrategy = CustomPointsStrategy(rules)

  def setBriscolaSuit(suit: String): Unit =
    this.briscolaSuit = suit

  def addCardToTable(player: PlayerModel, card: CardModel): Unit =
    cardsOnTable = cardsOnTable :+ (player, card)

  def clearTable(): Unit =
    cardsOnTable = List.empty

  def canPlayCard(playerHand: DeckModel, playedCard: CardModel): Boolean =
    handRuleStrategy.canPlayCard(cardsOnTable.map(_._2), playerHand, playedCard)

  def calculateWinningPlayer(): Option[PlayerModel] =
    playRuleStrategy.calculateWinningPlayer(cardsOnTable)

  def calculatePoints(): Int =
    pointsStrategy.calculatePoints(cardsOnTable.map((player, card) => card))

class FullEngineModel(
    val gameName: String,
    private val context: GameContext = GameContext()
) extends EngineModel
    with DeckManagement:

  override def playCard(player: PlayerModel, card: CardModel): Boolean =
    if player.eq(activePlayer) && context.canPlayCard(player.hand, card) then
      player.playCard(card)
      context.addCardToTable(player, card)
      nextPlayerTurn()
      true
    else false

  override def setWinRule(rule: WinRule): Unit =
    winRule = rule

  override def winningGamePlayers(): Team =
    winRule(this.teams, this.players).head

  override def computeTurn(): Unit =
    context.calculateWinningPlayer() match
      case Some(winningPlayer) =>
        addScoreToWinningPlayer(winningPlayer)
        setStartingPlayer(players.indexOf(winningPlayer))
      case None =>
        throw new IllegalStateException("No winning player found")

  private def addScoreToWinningPlayer(winningPlayer: PlayerModel): Unit =
    val points = context.calculatePoints()
    winningPlayer.increaseScore(points)
    context.clearTable()

  override def addPlayers(players: List[PlayerModel]): Unit =
    this.players = players
    setStartingPlayer(0)

  override def addTeams(teams: List[Team]): Unit =
    if teams.isEmpty then
      this.teams = players.map(player => List(player.name).asInstanceOf[Team])
      println(this.teams)
    else
      val existingPlayers = teams.flatMap(Team.toList).toSet
      val missingPlayers =
        players.map(p => p.name).filterNot(existingPlayers.contains)
      val missingTeams =
        missingPlayers.map(player => List(player).asInstanceOf[Team])
      this.teams = teams ++ missingTeams
      println(this.teams)

    if this.players.size == 4 && this.teams.size == 2 && this.teams.head.size == 2 then
      val playersOrder = List(
        this.teams.head(0),
        this.teams(1)(0),
        this.teams.head(1),
        this.teams(1)(1)
      )
      this.players = players.sortBy(player => playersOrder.indexOf(player.name))
      println(this.players)

  override def setStartingPlayer(index: Int): Unit =
    activePlayer = players(index)

  private def nextPlayerTurn(): Unit =
    val playerIndex = (players.indexOf(activePlayer) + 1) % players.size
    activePlayer = players(playerIndex)

  def setHandRule(rule: HandRule): Unit = context.setHandRule(rule)
  def setPlayRules(rules: List[PlayRule]): Unit = context.setPlayRules(rules)
  def setPointRules(rules: List[PointsRule]): Unit =
    context.setPointRules(rules)
  def setBriscolaSuit(suit: String): Unit = context.setBriscolaSuit(suit)
  def giveCardsToPlayers(handSize: Int): Unit =
    players.foreach(player => giveCardsToPlayer(player, handSize))
