package dsl.types

import dsl.syntax.SyntacticSugar.{SuitSyntacticSugar, TakesSyntacticSugar}
import dsl.types.Team.Team
import engine.model.{CardModel, DeckModel, PlayerModel}

object PlayerCount:
  opaque type PlayerCount = Int
  def apply(count: Int): PlayerCount =
    require(count >= 2 && count <= 4, "player count should be between 2 and 4")
    count

object Team:
  opaque type Team = List[String]
  def apply(team: List[String]): Team =
    require(team.size <= 4)
    team
  extension (team: Team)
    def map[B](f: String => B): List[B] = for t <- team yield f(t)
    def size: Int = team.size
    def toSet: Set[String] = team.toSet
    def zipWithIndex: List[(String, Int)] = team.zipWithIndex
    def toList: List[String] = team.toList
    def apply(n: Int): String = team.apply(n)
    def reduce(f: (String, String) => String): String = team.reduce(f)

object HandSize:
  opaque type HandSize = Int
  def apply(size: Int): HandSize =
    require(size >= 3 && size <= 10, "hand size should be between 3 and 10")
    size

  extension (handSize: HandSize) def value: Int = handSize

object Suits:
  opaque type Suits = List[String]
  def apply(suits: List[String]): Suits =
    require(suits.size == 4, "only 4 suits are allowed")
    require(suits.distinct.size == 4, "suits must be distinct")
    suits

  extension (suits: Suits)
    def size: Int = suits.size
    def distinct: List[String] = suits.distinct
    def map[B](f: String => B): List[B] = suits.map(f)
    def flatMap[B](f: String => List[B]): List[B] = suits.flatMap(f)
    def foreach(f: String => Unit): Unit = suits.foreach(f)
    def contains(suit: String): Boolean = suits.contains(suit)

object PointsRule:
  opaque type PointsRule = (String, String) => Int
  def apply(rule: (String, String) => Int): PointsRule = rule
  extension (rule: PointsRule)
    def apply(name: String, suit: String): Int = rule(name, suit)

/** (CardsOnTable, PlayerCards, CardPlayed) => CardPlayed can be played
  */
object HandRule:
  opaque type HandRule = (List[CardModel], DeckModel, CardModel) => Boolean
  def freeStart(using cardsOnTable: List[CardModel]): Boolean =
    cardsOnTable.isEmpty
  def startWithHigherCard(using
      cardsOnTable: List[CardModel],
      playerHand: DeckModel,
      playedCard: CardModel
  ): Boolean =
    cardsOnTable.isEmpty &&
      !playerHand.view.exists(_.rank > playedCard.rank)
  def followFirstSuit(using
      cardsOnTable: List[CardModel],
      playerHand: DeckModel,
      playedCard: CardModel
  ): Boolean =
    cardsOnTable.nonEmpty &&
      (cardsOnTable.head.suit == playedCard.suit ||
        !playerHand.view.exists(_.suit == cardsOnTable.head.suit))
  def followPreviousSuit(using
      cardsOnTable: List[CardModel],
      playerHand: DeckModel,
      playedCard: CardModel
  ): Boolean =
    val previousSuit = cardsOnTable.lastOption.map(_.suit).get
    previousSuit == playedCard.suit ||
    !playerHand.view.exists(_.suit == cardsOnTable.head.suit)

  def marafoneRuleset(using
      cardsOnTable: List[CardModel],
      playerHand: DeckModel,
      playedCard: CardModel
  ): Boolean =
    freeStart ||
      followFirstSuit

  def apply(
      rule: (List[CardModel], DeckModel, CardModel) => Boolean
  ): HandRule = rule

  extension (rule: HandRule)
    def apply(
        cardsOnTable: List[CardModel],
        playerHand: DeckModel,
        playedCard: CardModel
    ): Boolean =
      rule(cardsOnTable, playerHand, playedCard)

  extension (rule: Boolean)
    def or(other: Boolean): Boolean =
      rule || other
    def and(other: Boolean): Boolean =
      rule && other

object PlayRule:
  opaque type PlayRule = List[(PlayerModel, CardModel)] => Option[PlayerModel]
  def apply(
      rule: List[(PlayerModel, CardModel)] => Option[PlayerModel]
  ): PlayRule =
    rule
  extension (rule: PlayRule)
    def apply(
        cardsOnTable: List[(PlayerModel, CardModel)]
    ): Option[PlayerModel] =
      rule(cardsOnTable)
  extension (rule: List[(PlayerModel, CardModel)] => Option[PlayerModel])
    infix def prevailsOn(
        other: List[(PlayerModel, CardModel)] => Option[PlayerModel]
    ): List[(PlayerModel, CardModel)] => Option[PlayerModel] =
      (cardsOnTable: List[(PlayerModel, CardModel)]) =>
        rule(cardsOnTable) orElse other(cardsOnTable)

object WinRule:
  opaque type WinRule = (List[Team], List[PlayerModel]) => List[Team]
  def highest(using
      teams: List[Team],
      players: List[PlayerModel]
  ): List[Team] =
    val playerScores = players.map(player => player.name -> player.score).toMap

    // Calculate the total score for each team
    val teamsWithScores = teams.map(team =>
      val totalScore =
        team.map(playerName => playerScores.getOrElse(playerName, 0)).sum
      (team, totalScore)
    )

    val orderedTeams = teamsWithScores.sortBy(-_._2).map(_._1)
    orderedTeams

  def lowest(using
      teams: List[Team],
      players: List[PlayerModel]
  ): List[Team] =
    val playerScores = players.map(player => player.name -> player.score).toMap

    val teamsWithScores = teams.map(team =>
      val totalScore =
        team.map(playerName => playerScores.getOrElse(playerName, 0)).sum
      (team, totalScore)
    )

    val orderedTeams = teamsWithScores.sortBy(_._2).map(_._1)
    orderedTeams

  def apply(
      rule: (List[Team], List[PlayerModel]) => List[Team]
  ): WinRule =
    rule

  extension (rule: WinRule)
    def apply(
        teams: List[Team],
        players: List[PlayerModel]
    ): List[Team] =
      rule(teams, players)
