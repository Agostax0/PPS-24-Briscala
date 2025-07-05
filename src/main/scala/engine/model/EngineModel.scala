package engine.model

import dsl.types.*
import dsl.types.HandRule.HandRule
import dsl.types.PlayRule.PlayRule
import dsl.types.PointsRule.PointsRule
import dsl.types.Team.Team
import dsl.types.WinRule.WinRule

import scala.collection.immutable.List

/** The GameContext class encapsulates the rules and strategies for the game
  * along with its current state.
  */
class GameContext:
  private var handRuleStrategy: HandRuleStrategy = DefaultHandRuleStrategy()
  private var playRuleStrategy: PlayRuleStrategy = DefaultPlayRuleStrategy()
  private var pointsStrategy: PointsStrategy = DefaultPointsStrategy()
  private var winRuleStrategy: WinRuleStrategy = DefaultWinRuleStrategy()

  /** The cards currently on the table, represented as a list of tuples
    * containing the player and their played card.
    */
  var cardsOnTable: List[(PlayerModel, CardModel)] = List.empty

  /** The suit of the briscola card. */
  var briscolaSuit: String = ""

  /** Sets the hand rule strategy for the game context.
    *
    * @param rule
    *   The hand rule to be applied.
    */
  def setHandRule(rule: HandRule): Unit =
    handRuleStrategy = CustomHandRuleStrategy(rule)

  /** Sets the play rules strategy for the game context.
    *
    * @param rules
    *   A list of play rules to be applied.
    */
  def setPlayRules(rules: List[PlayRule]): Unit =
    playRuleStrategy = CustomPlayRuleStrategy(rules)

  /** Sets the points rules strategy for the game context.
    *
    * @param rules
    *   A list of points rules to be applied.
    */
  def setPointRules(rules: List[PointsRule]): Unit =
    pointsStrategy = CustomPointsStrategy(rules)

  /** Sets the win rule strategy for the game context.
    *
    * @param rule
    *   the win rule to be applied.
    */
  def setWinRule(rule: WinRule): Unit =
    winRuleStrategy = CustomWinRuleStrategy(rule)

  /** Sets the suit of the briscola card.
    *
    * @param suit
    *   the suit of the briscola card.
    */
  def setBriscolaSuit(suit: String): Unit =
    this.briscolaSuit = suit

  /** Adds a card played by a player to the table.
    *
    * @param player
    *   the player who played the card
    * @param card
    *   the card played by the player
    */
  def addCardToTable(player: PlayerModel, card: CardModel): Unit =
    cardsOnTable = cardsOnTable :+ (player, card)

  /** Clears the cards currently on the table. */
  def clearTable(): Unit =
    cardsOnTable = List.empty

  /** Checks if a player can play a specific card based on the current hand and
    * the rules defined in the game context.
    * @param playerHand
    *   the hand of the player
    * @param playedCard
    *   the card the player wants to play
    * @return
    *   true if the player can play the card, false otherwise
    */
  def canPlayCard(playerHand: DeckModel, playedCard: CardModel): Boolean =
    handRuleStrategy.canPlayCard(cardsOnTable.map(_._2), playerHand, playedCard)

  /** Calculates the winning player for the current turn based on the cards on
    * the table and the play rules defined in the game context.
    *
    * @param table
    *   the list of cards on the table, represented as tuples of (player, card)
    * @return
    *   the winning player if one exists, otherwise None
    */
  def calculateTurn(
      table: List[(PlayerModel, CardModel)] = cardsOnTable
  ): Option[PlayerModel] =
    playRuleStrategy.calculateWinningPlayer(table)

  /** Calculates the points for the current turn based on the cards on the table
    * and the points rules defined in the game context.
    *
    * @param table
    *   the list of cards on the table, represented as tuples of (player, card)
    * @return
    *   the total points for the turn
    */
  def calculatePoints(
      table: List[(PlayerModel, CardModel)] = cardsOnTable
  ): Int =
    pointsStrategy.calculatePoints(table.map((player, card) => card))

  /** Calculates the winning team for the game based on the teams and players
    * defined in the game context.
    *
    * @param teams
    *   the list of teams in the game
    * @param players
    *   the list of players in the game
    * @return
    *   the winning team
    */
  def calculateWinner(teams: List[Team], players: List[PlayerModel]): List[Team] =
    winRuleStrategy.orderedTeam(teams, players)

/** * The EngineModel trait defines the core functionalities of the game engine,
  * including player management, team management, and game mechanics.
  */
trait EngineModel:
  /** List of players in the game.
    */
  var players: List[PlayerModel] = List.empty

  /** List of teams in the game.
    */
  var teams: List[Team] = List.empty

  /** The active player in the current turn.
    */
  var activePlayer: PlayerModel = _

  /** Adds players to the game.
    *
    * @param players
    *   the list of players to be added
    */
  def addPlayers(players: List[PlayerModel]): Unit

  /** Adds teams to the game.
    *
    * @param teams
    *   the list of teams to be added
    */
  def addTeams(teams: List[Team]): Unit

  /** Computes the turn of the game, determining the winning player, adding the
    * points to their score and resetting the table for the next turn.
    */
  def computeTurn(): Unit

  /** Sets the starting player for the next turn.
    *
    * @param index
    *   the index of the player to be set as the starting player
    */
  def setStartingPlayer(index: Int): Unit

  /** Plays a card for the given player, checking if the player is the active
    * player and if the card can be played according to the game rules.
    *
    * @param player
    *   the player who is playing the card
    * @param card
    *   the card to be played
    * @return
    *   true if the card was played successfully, false otherwise
    */
  def playCard(player: PlayerModel, card: CardModel): Boolean

  /** Plays a card for the bot player, generating a card based on the game
    * context.
    *
    * @param bot
    *   the bot player who is playing the card
    * @return
    *   the card played by the bot
    */
  def botPlayCard(bot: BotPlayerModel): CardModel

  /** Returns the winning team of the game based on the game context.
    *
    * @return
    *   the winning team
    */
  def winningGamePlayers(): List[(Team,Int)]

/** Implements the EngineModel trait and provides a complete game engine for
  * managing players, teams, deck and game mechanics.
  * @param gameName
  *   the name of the game
  * @param context
  *   the game context containing rules and strategies
  */
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

  override def winningGamePlayers(): List[(Team, Int)] = 
    val teams = context.calculateWinner(this.teams, this.players)
    val scoreMap: Map[String, Int] = players.map(p => p.name -> p.score).toMap

    teams.map ( team =>
      val totalScore = team.map(name => scoreMap.getOrElse(name, 0)).sum
      (team, totalScore)
    )
  

  private def addScoreToWinningPlayer(winningPlayer: PlayerModel): Unit =
    val points = context.calculatePoints()
    winningPlayer.increaseScore(points)

  private def nextPlayerTurn(): Unit =
    val playerIndex = (players.indexOf(activePlayer) + 1) % players.size
    activePlayer = players(playerIndex)

  /** Sets the game rules for the game context.
    *
    * @param rule
    *   the hand rule to be applied
    */
  def setHandRule(rule: HandRule): Unit = context.setHandRule(rule)

  /** Sets the play rules for the game context.
    *
    * @param rules
    *   the list of play rules to be applied
    */
  def setPlayRules(rules: List[PlayRule]): Unit = context.setPlayRules(rules)

  /** Sets the points rules for the game context.
    *
    * @param rules
    *   the list of points rules to be applied
    */
  def setPointRules(rules: List[PointsRule]): Unit =
    context.setPointRules(rules)

  /** Sets the win rule for the game context.
    *
    * @param rule
    *   the win rule to be applied
    */
  def setWinRule(rule: WinRule): Unit = context.setWinRule(rule)

  /** Sets the briscola suit for the game context.
    *
    * @param suit
    *   the briscola suit to be added
    */
  def setBriscolaSuit(suit: String): Unit = context.setBriscolaSuit(suit)

  /** Gives a specified number of cards to each player from the deck.
    *
    * @param handSize
    *   the number of cards to be given to each player
    */
  def giveCardsToPlayers(handSize: Int): Unit =
    if deck.view.size > handSize * players.size then
      players.foreach(player => giveCardsToPlayer(player, handSize))
