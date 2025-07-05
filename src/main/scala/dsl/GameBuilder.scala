package dsl

import dsl.types.*
import dsl.types.HandRule.HandRule
import dsl.types.HandSize.HandSize
import dsl.types.PlayRule.PlayRule
import dsl.types.PlayerCount.PlayerCount
import dsl.types.PointsRule.PointsRule
import dsl.types.Suits.Suits
import dsl.types.Team.Team
import dsl.types.WinRule.WinRule
import dsl.types.{
  HandRule,
  HandSize,
  PlayRule,
  PlayerCount,
  PointsRule,
  Suits,
  Team,
  WinRule
}
import engine.model.{
  BotPlayerModel,
  BotType,
  CardModel,
  DeckModel,
  FullEngineModel,
  PlayerModel
}

/** GameBuilder is a trait that defines the methods to build a game.
  */
sealed trait GameBuilder:
  /** The name of the game.
    */
  val gameName: String

  /** Adds a player to the game.
    * @param name
    *   the name of the player
    * @return
    *   the GameBuilder instance with the added player
    */
  def addPlayer(name: String): GameBuilder
  /** Adds a bot player to the game.
    * @param name
    *   the name of the bot player
    * @param botType
    *   the type of the bot
    * @return
    *   the GameBuilder instance with the added bot player
    */
  def addBotPlayer(name: String, botType: BotType): GameBuilder
  /** Sets the number of players in the game.
    * @param n
    *   the number of players
    * @return
    *   the GameBuilder instance with the set number of players
    */
  def setPlayers(n: Int): GameBuilder
  /** Sets the suits of the cards in the game.
    * @param suits
    *   the list of suits
    * @return
    *   the GameBuilder instance with the set suits
    */
  def setSuits(suits: List[String]): GameBuilder
  /** Sets the ranks of the cards in the game.
    * @param ranks
    *   the list of ranks
    * @return
    *   the GameBuilder instance with the set ranks
    */
  def setRanks(ranks: List[String]): GameBuilder
  /** Sets the size of the hands for each player.
    * @param handSize
    *   the size of the hands
    * @return
    *   the GameBuilder instance with the set hand size
    */
  def setPlayersHands(handSize: Int): GameBuilder
  /** Sets the starting player of the game.
    * @param name
    *   the name of the starting player
    * @return
    *   the GameBuilder instance with the set starting player
    */
  def setStartingPlayer(name: String): GameBuilder
  /** Sets the point rules for the game.
    * @param rule
    *   the points rule to be set
    * @return
    *   the GameBuilder instance with the set point rules
    */
  def setPointRule(rule: PointsRule): GameBuilder
  /** Sets the play rules for the game.
    * @param rule
    *   the play rule to be set
    * @return
    *   the GameBuilder instance with the set play rules
    */
  def setPlayRule(rule: PlayRule): GameBuilder
  /** Sets the hand rule for the game.
    * @param rule
    *   the hand rule to be set
    * @return
    *   the GameBuilder instance with the set hand rule
    */
  def setHandRule(rule: HandRule): GameBuilder
  /** Sets the win rule for the game.
    * @param rule
    *   the win rule to be set
    * @return
    *   the GameBuilder instance with the set win rule
    */
  def setWinRule(rule: WinRule): GameBuilder
  /** Adds a team to the game.
    * @param names
    *   the list of player names in the team
    * @return
    *   the GameBuilder instance with the added team
    * @throws IllegalArgumentException
    *   if any player in the team does not exist or is already in another team
    */
  def addTeam(names: List[String]): GameBuilder
  /** Sets the suit of the briscola card.
      * @param suit
      *   the suit of the briscola card
      * @return
      *   the GameBuilder instance with the set briscola suit
      * @throws IllegalArgumentException
      *   if the suit is not defined in the game
      */
  def setBriscolaSuit(suit: String): GameBuilder
  /** Returns the suit of the briscola card.
    * @return
    *   the suit of the briscola card
    */
  def briscola: String = ""
  /** Builds the game model.
    * @return
    *   the FullEngineModel representing the game
    * @throws IllegalArgumentException
    *   if the number of players does not match the set player count or if any
    *   other required field is not set correctly
    */
  def build(): FullEngineModel

object GameBuilder:
  def apply(gameName: String): GameBuilder = GameBuilderImpl(gameName)

  private class GameBuilderImpl(val gameName: String) extends GameBuilder:
    private var players: List[PlayerModel] = List.empty
    private var playerCount: PlayerCount = _
    private var suits: Suits = _
    private var ranks: List[String] = List.empty
    private var handSize: HandSize = _
    private var startingPlayerIndex: Option[Int] = None
    private var pointRules: Option[List[PointsRule]] = None
    private var playRules: Option[List[PlayRule]] = None
    private var winRule: Option[WinRule] = None
    private var teams: List[Team] = List.empty
    private var handRule: Option[HandRule] = None
    private var briscolaSuit: String = ""

    override def briscola: String = this.briscolaSuit

    override def addPlayer(name: String): GameBuilder =
      players = players :+ PlayerModel(name)
      this

    override def addBotPlayer(name: String, botType: BotType): GameBuilder =
      players = players :+ BotPlayerModel(name, botType)
      this

    override def addTeam(names: List[String]): GameBuilder =
      //check whether the player exist
      names.foreach(newPlayerName =>
        val playerExists = players.exists(p => p.name == newPlayerName)
        if !playerExists then
          throw IllegalArgumentException("Player/s doesn't exists")
      )
      //check whether a player is already inside another team
      if names.intersect(teams.flatMap(t => t.toList)).nonEmpty then
        throw IllegalArgumentException("Players already inside another team")

      teams = teams :+ Team(names)
      this

    override def setPlayers(n: Int): GameBuilder =
      playerCount = PlayerCount(n)
      this

    override def setSuits(suits: List[String]): GameBuilder =
      this.suits = Suits(suits)
      this

    override def setRanks(ranks: List[String]): GameBuilder =
      this.ranks = ranks
      this

    override def setPlayersHands(handSize: Int): GameBuilder =
      this.handSize = HandSize(handSize)
      this

    override def setStartingPlayer(name: String): GameBuilder =
      if players.isEmpty || !players.map(_.name).contains(name) then
        throw new IllegalArgumentException("Player not found")

      this.startingPlayerIndex = this.startingPlayerIndex match
        case Some(index) =>
          throw new IllegalArgumentException("Starting player already set")
        case _ => Some(players.map(_.name).indexOf(name))
      this

    override def setPointRule(rule: PointsRule): GameBuilder =
      val newRules = this.pointRules match
        case Some(rules) => rules :+ rule
        case None        => List(rule)
      this.pointRules = Some(newRules)
      this

    override def setPlayRule(rule: PlayRule): GameBuilder =
      val newRules = this.playRules match
        case Some(rules) => rules :+ rule
        case None        => List(rule)
      this.playRules = Some(newRules)
      this

    override def setWinRule(rule: WinRule): GameBuilder =
      this.winRule = Some(rule)
      this

    private def briscola_=(newBriscolaSuit: String): Unit =
      if !this.suits.contains(newBriscolaSuit) then
        throw new IllegalArgumentException("Briscola suit is not defined")
      else this.briscolaSuit = newBriscolaSuit

    override def setBriscolaSuit(suit: String): GameBuilder =
      briscola = suit
      this

    override def setHandRule(rule: HandRule): GameBuilder =
      this.handRule = Some(rule)
      this

    override def build(): FullEngineModel =
      if !playerCount.equals(PlayerCount(players.size)) then
        throw new IllegalArgumentException("Incorrect number of players joined")

      val game = FullEngineModel(gameName)
      game.addPlayers(players)
      game.addTeams(teams)
      game.createDeck(suits, ranks)
      game.giveCardsToPlayers(handSize.value)
      game.setStartingPlayer(startingPlayerIndex.getOrElse(0))
      game.setBriscolaSuit(briscolaSuit)
      handRule match
        case Some(rule) => game.setHandRule(rule)
        case None       => ()
      pointRules match
        case Some(rules) => game.setPointRules(rules)
        case None        => ()
      playRules match
        case Some(rules) => game.setPlayRules(rules)
        case None        => ()
      winRule match
        case Some(rule) => game.setWinRule(rule)
        case None       => ()
      game

    override def equals(obj: Any): Boolean =
      obj match
        case that: SimpleGameBuilder =>
          val player = players.head
          val hand = DeckModel()
          val card = CardModel("Ace", 11, briscola)
          hand.addCard(card)

          val checkValues = this.gameName == that.gameName &&
            this.players.forall(p1 =>
              that.players.map(p2 => p2.name).contains(p1.name)
            ) &&
            this.playerCount == that.playerCount &&
            this.teams.forall(team => that.teams.contains(team)) &&
            this.suits.equals(that.suits) &&
            this.ranks.equals(that.ranks) &&
            this.briscola == that.briscola &&
            this.handSize == that.handSize &&
            this.startingPlayerIndex == that.startingPlayerIndex
          val checkPointRules =
            (this.pointRules.isEmpty && that.pointRules.isEmpty) ||
              this.pointRules.get.head("Ace", "Cups") ==
              that.pointRules.get.head("Ace", "Cups")
          val checkHandRule =
            (this.handRule.isEmpty && that.handRule.isEmpty) ||
              (this.handRule.get(List.empty, hand, card) ==
                that.handRule.get(List.empty, hand, card))
          val checkPlayRules =
            (this.playRules.isEmpty && that.playRules.isEmpty) ||
              (this.playRules.get.head(List((player, card))) ==
                that.playRules.get.head(List((player, card))))
          val checkWinRule = (this.winRule.isEmpty && that.winRule.isEmpty) ||
            (this.winRule.get(teams, players) ==
              that.winRule.get(teams, players))

          checkValues && checkPointRules &&
          checkHandRule && checkPlayRules && checkWinRule
        case _ => false

class SimpleGameBuilder(val _gameName: String = "Simple Game")
    extends GameBuilder:
  var players: List[PlayerModel] = List.empty
  var playerCount: PlayerCount = _
  var suits: Suits = _
  var ranks: List[String] = List.empty
  var handSize: HandSize = _
  var startingPlayerIndex: Option[Int] = None
  var pointRules: Option[List[PointsRule]] = None
  var playRules: Option[List[PlayRule]] = None
  var winRule: Option[WinRule] = None
  private var briscolaSuit: String = ""
  var teams: List[List[String]] = List.empty
  var handRule: Option[HandRule] = None

  override def briscola: String = this.briscolaSuit

  override val gameName: String = _gameName

  override def addPlayer(name: String): GameBuilder =
    players = players :+ PlayerModel(name)
    this

  override def addBotPlayer(name: String, botType: BotType): GameBuilder =
    players = players :+ BotPlayerModel(name, botType)
    this

  override def addTeam(names: List[String]): GameBuilder =
    //check whether the player exist
    names.foreach(newPlayerName =>
      val playerExists = players.exists(p => p.name == newPlayerName)
      if !playerExists then
        throw IllegalArgumentException("Player/s doesn't exists")
    )
    //check whether a player is already inside another team
    if names.intersect(teams.flatMap(t => t.toList)).nonEmpty then
      throw IllegalArgumentException("Players already inside another team")

    teams = teams :+ names
    this

  override def setPlayers(n: Int): GameBuilder =
    playerCount = PlayerCount(n)
    this

  override def setSuits(suits: List[String]): GameBuilder =
    this.suits = Suits(suits)
    this

  override def setRanks(ranks: List[String]): GameBuilder =
    this.ranks = ranks
    this

  override def setPlayersHands(handSize: Int): GameBuilder =
    this.handSize = HandSize(handSize)
    this

  override def setStartingPlayer(name: String): GameBuilder =
    if players.isEmpty || !players.map(_.name).contains(name) then
      throw new IllegalArgumentException("Player not found")

    this.startingPlayerIndex = this.startingPlayerIndex match
      case Some(index) =>
        throw new IllegalArgumentException("Starting player already set")
      case _ => Some(players.map(_.name).indexOf(name))
    this

  override def setPointRule(rule: PointsRule): GameBuilder =
    val newRules = this.pointRules match
      case Some(rules) => rules :+ rule
      case None        => List(rule)
    this.pointRules = Some(newRules)
    this

  override def setPlayRule(rule: PlayRule): GameBuilder =
    val newRules = this.playRules match
      case Some(rules) => rules :+ rule
      case None        => List(rule)
    this.playRules = Some(newRules)
    this

  override def setWinRule(rule: WinRule): GameBuilder =
    this.winRule = Some(rule)
    this

  override def setBriscolaSuit(suit: String): GameBuilder =
    this.briscolaSuit = suit
    this

  override def setHandRule(rule: HandRule): GameBuilder =
    this.handRule = Some(rule)
    this

  override def build(): FullEngineModel =
    FullEngineModel(gameName)
