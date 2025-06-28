package dsl

import dsl.types.{HandSize, PlayerCount, PointsRule, Suits, Team}
import dsl.types.{HandRule, HandSize, PlayRule, PlayerCount, PointsRule, Suits}
import engine.model.{FullEngineModel, PlayerModel}

sealed trait GameBuilder:
  val gameName: String
  def addPlayer(name: String): GameBuilder
  def setPlayers(n: Int): GameBuilder
  def addSuits(suits: List[String]): GameBuilder
  def addRanks(ranks: List[String]): GameBuilder
  def setPlayersHands(handSize: Int): GameBuilder
  def setStartingPlayer(name: String): GameBuilder
  def addPointRule(rule: PointsRule): GameBuilder
  def addPlayRule(rule: PlayRule): GameBuilder
  def addHandRule(rule: HandRule): GameBuilder
  def addBriscolaSuit(suit: String): GameBuilder
  def addTeam(names: List[String]): GameBuilder
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
    private var pointRules: List[PointsRule] = List.empty
    private var playRules: List[PlayRule] = List.empty
    private var briscolaSuit: String = ""
    private var teams: List[Team] = List.empty
    private var handRule: Option[HandRule] = None

    override def addPlayer(name: String): GameBuilder =
      players = players :+ PlayerModel(name)
      this

    override def addTeam(names: List[String]): GameBuilder = {
      //check whether the player exist
      names.foreach( newPlayerName =>
        val playerExists = players.exists(p => p.name == newPlayerName)
        if !playerExists then
          throw IllegalArgumentException("Player/s doesn't exists")
      )
      //check whether a player is already inside another team
      if  teams.exists(team => names.toSet.subsetOf(team.toSet)) then
        throw IllegalArgumentException("Players already inside another team")

      teams = teams :+ Team(names)
      this
    }

    override def setPlayers(n: Int): GameBuilder =
      playerCount = PlayerCount(n)
      this

    override def addSuits(suits: List[String]): GameBuilder =
      this.suits = Suits(suits)
      this

    override def addRanks(ranks: List[String]): GameBuilder =
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

    override def addPointRule(rule: PointsRule): GameBuilder =
      this.pointRules = this.pointRules :+ rule
      this

    override def addPlayRule(rule: PlayRule): GameBuilder =
      this.playRules = this.playRules :+ rule
      this

    override def addBriscolaSuit(suit: String): GameBuilder =
      if !this.suits.contains(suit) then
        throw new IllegalArgumentException("Briscola suit is not defined")
      else this.briscolaSuit = suit
      this

    override def addHandRule(rule: HandRule): GameBuilder =
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
      game.setPointRules(pointRules)
      game.setPlayRules(playRules)
      game.setBriscolaSuit(briscolaSuit)
      handRule match
        case Some(rule) => game.setHandRules(rule)
        case None =>
      game

class SimpleGameBuilder extends GameBuilder:
  var players: List[PlayerModel] = List.empty
  var playerCount: PlayerCount = _
  var suits: Suits = _
  var ranks: List[String] = List.empty
  var handSize: HandSize = _
  var startingPlayerIndex: Option[Int] = None
  var pointRules: List[PointsRule] = List.empty
  var playRules: List[PlayRule] = List.empty
  var briscolaSuit: String = ""
  var teams: List[List[String]] = List.empty
  var handRule: Option[HandRule] = None

  override val gameName: String = "Simple Game"

  override def addPlayer(name: String): GameBuilder =
    players = players :+ PlayerModel(name)
    this

  override def addTeam(names: List[String]): GameBuilder = {
    //check whether the player exist
    names.foreach(newPlayerName =>
      val playerExists = players.exists(p => p.name == newPlayerName)
      if !playerExists then
        throw IllegalArgumentException("Player/s doesn't exists")
    )
    //check whether a player is already inside another team
    if teams.exists(team => names.toSet.subsetOf(team.toSet)) then
      throw IllegalArgumentException("Players already inside another team")

    teams = teams :+ names
    this
  }

  override def setPlayers(n: Int): GameBuilder =
    playerCount = PlayerCount(n)
    this

  override def addSuits(suits: List[String]): GameBuilder =
    this.suits = Suits(suits)
    this

  override def addRanks(ranks: List[String]): GameBuilder =
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

  override def addPointRule(rule: PointsRule): GameBuilder =
    this.pointRules = this.pointRules :+ rule
    this

  override def addPlayRule(rule: PlayRule): GameBuilder =
    this.playRules = this.playRules :+ rule
    this

  override def addBriscolaSuit(suit: String): GameBuilder =
    this.briscolaSuit = suit
    this

  override def addHandRule(rule: HandRule): GameBuilder =
    this.handRule = Some(rule)
    this

  override def build(): FullEngineModel =
    FullEngineModel(gameName)
