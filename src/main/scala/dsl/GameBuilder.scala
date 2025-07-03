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
import dsl.types.{HandRule, HandSize, PlayRule, PlayerCount, PointsRule, Suits, Team, WinRule}
import engine.model.{BotPlayerModel, BotType, FullEngineModel, PlayerModel}

sealed trait GameBuilder:
  val gameName: String
  def addPlayer(name: String): GameBuilder
  def addBotPlayer(name: String, botType: BotType): GameBuilder
  def setPlayers(n: Int): GameBuilder
  def setSuits(suits: List[String]): GameBuilder
  def setRanks(ranks: List[String]): GameBuilder
  def setPlayersHands(handSize: Int): GameBuilder
  def setStartingPlayer(name: String): GameBuilder
  def setPointRule(rule: PointsRule): GameBuilder
  def setPlayRule(rule: PlayRule): GameBuilder
  def setHandRule(rule: HandRule): GameBuilder
  def setWinRule(rule: WinRule): GameBuilder
  def addTeam(names: List[String]): GameBuilder
  def setBriscolaSuit(suit: String): GameBuilder
  def briscola: String = ""
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
        case None => List(rule)
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
        case None => ()
      playRules match
        case Some(rules) => game.setPlayRules(rules)
        case None => ()
      winRule match
        case Some(rule) => game.setWinRule(rule)
        case None => ()
      game

class SimpleGameBuilder extends GameBuilder:
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

  override val gameName: String = "Simple Game"

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
      case None => List(rule)
    this.pointRules = Some(newRules)
    this

  override def setPlayRule(rule: PlayRule): GameBuilder =
    val newRules = this.playRules match
      case Some(rules) => rules :+ rule
      case None => List(rule)
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
