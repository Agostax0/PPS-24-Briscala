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
