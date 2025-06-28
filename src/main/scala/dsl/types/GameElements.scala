package dsl.types

import engine.model.{CardModel, DeckModel, PlayerModel}

opaque type PlayerCount = Int
object PlayerCount:
  def apply(count: Int): PlayerCount =
    require(count >= 2 && count <= 4, "player count should be between 2 and 4")
    count

opaque type Team = List[String]
object Team:
  def apply(team: List[String]): Team =
    require(team.size <= 4)
    team
  extension (team: Team)
    def size: Int = team.size
    def toSet: Set[String] = team.toSet
    def zipWithIndex: List[(String, Int)] = team.zipWithIndex
    def toList: List[String] = team.toList
    def apply(n: Int): String = team.apply(n)

opaque type HandSize = Int
object HandSize:
  def apply(size: Int): HandSize =
    require(size >= 3 && size <= 10, "hand size should be between 3 and 10")
    size

  extension (handSize: HandSize) def value: Int = handSize

opaque type Suits = List[String]
object Suits:
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

opaque type PointsRule = (String, String) => Int
object PointsRule:
  def apply(rule: (String, String) => Int): PointsRule = rule
  extension (rule: PointsRule)
    def apply(name: String, suit: String): Int = rule(name, suit)

/** (CardsOnTable, PlayerCards, CardPlayed) => CardPlayed can be played
  */
opaque type HandRule = (List[CardModel], DeckModel, CardModel) => Boolean
object HandRule:
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

opaque type PlayRule = List[(PlayerModel, CardModel)] => Option[PlayerModel]
object PlayRule:

  def apply(
      rule: List[(PlayerModel, CardModel)] => Option[PlayerModel]
  ): PlayRule =
    rule

  def highestBriscolaTakes(using
      cardsOnTable: List[(PlayerModel, CardModel)]
  )(using
      briscola: String
  ): Option[PlayerModel] =
    cardsOnTable
      .filter(_._2.suit equals briscola)
      .sortBy(_._2.rank)
      .map(_._1)
      .headOption

  def highestCardTakes(using
      cardsOnTable: List[(PlayerModel, CardModel)]
  ): Option[PlayerModel] =
    val suit = cardsOnTable.firstCardPlayed.get._2.suit
    cardsOnTable
      .filter(_._2.suit == suit)
      .sortBy(_._2.rank)
      .map(_._1)
      .headOption
  extension (rule: PlayRule)
    def apply(
        cardsOnTable: List[(PlayerModel, CardModel)]
    ): Option[PlayerModel] =
      rule(cardsOnTable)
  extension (rule: List[(PlayerModel, CardModel)] => Option[PlayerModel])
    infix def prevails(
        other: List[(PlayerModel, CardModel)] => Option[PlayerModel]
    ): List[(PlayerModel, CardModel)] => Option[PlayerModel] =
      (cardsOnTable: List[(PlayerModel, CardModel)]) =>
        rule(cardsOnTable) orElse other(cardsOnTable)

  extension (cardsOnTable: List[(PlayerModel, CardModel)])
    def firstCardPlayed: Option[(PlayerModel, CardModel)] =
      cardsOnTable.headOption
    def lastCardPlayed: Option[(PlayerModel, CardModel)] =
      cardsOnTable.lastOption
