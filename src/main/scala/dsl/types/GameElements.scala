package dsl.types

import dsl.syntax.SyntacticSugar.{SuitSyntacticSugar, TakesSyntacticSugar}
import dsl.types.Team.Team
import engine.model.{CardModel, DeckModel, PlayerModel}

object PlayerCount:
  /** Represents the number of players in a game.
    *
    * It can be between 2 and 4, inclusive.
    */
  opaque type PlayerCount = Int

  /** Creates a PlayerCount value, ensuring it is between 2 and 4.
    *
    * @param count
    *   the number of players
    * @return
    *   a PlayerCount value
    * @throws IllegalArgumentException
    *   if count is not between 2 and 4
    */
  def apply(count: Int): PlayerCount =
    require(count >= 2 && count <= 4, "player count should be between 2 and 4")
    count

object Team:
  /** Represents a team of players.
    *
    * A team can have at most 4 players.
    */
  opaque type Team = List[String]

  /** Creates a Team value, ensuring it has at most 4 players.
    *
    * @param team
    *   the list of player names in the team
    * @return
    *   a Team value
    * @throws IllegalArgumentException
    *   if the team size is greater than 4
    */
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
    def equals(other: Team): Boolean =
      team.size == other.size && team.forall(player => other.contains(player))
    def contains(player: String): Boolean = team.contains(player)

object HandSize:
  /** Represents the size of a player's hand in a card game.
    *
    * A hand can have between 3 and 10 cards, inclusive.
    */
  opaque type HandSize = Int

  /** Creates a HandSize value, ensuring it is between 3 and 10.
    *
    * @param size
    *   the number of cards in hand
    * @return
    *   a HandSize value
    * @throws IllegalArgumentException
    *   if size is not between 3 and 10
    */
  def apply(size: Int): HandSize =
    require(size >= 3 && size <= 10, "hand size should be between 3 and 10")
    size

  extension (handSize: HandSize) def value: Int = handSize

object Suits:
  /** Represents a collection of 4 distinct suits.
    */
  opaque type Suits = List[String]

  /** Creates a Suits value, ensuring it contains exactly 4 distinct suits.
    *
    * @param suits
    *   the list of suits
    * @return
    *   a Suits value
    * @throws IllegalArgumentException
    *   if the number of suits is not 4 or if they are not distinct
    */
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
  /** Represents a rule for calculating points based on card name and suit. The
    * rule is a function that takes the cards' name and suit, and returns the
    * points associated with that card.
    *
    * The function should return an Int value representing the points.
    *
    * Example: `("King", "Hearts") => 10`
    */
  opaque type PointsRule = (String, String) => Int

  /** Creates a PointsRule value.
    *
    * @param rule
    *   the function that calculates points based on player name and suit
    * @return
    *   a PointsRule value
    */
  def apply(rule: (String, String) => Int): PointsRule = rule
  extension (rule: PointsRule)
    def apply(name: String, suit: String): Int = rule(name, suit)

object HandRule:
  /** Represents a rule for playing a card from a player's hand.
    *
    * The rule is a function that takes the current cards on the table, the
    * player's hand, and the card being played, and returns a Boolean indicating
    * whether the play is valid according to the rule.
    */
  opaque type HandRule = (List[CardModel], DeckModel, CardModel) => Boolean

  /** If the player starts the turn, they can play any card from their hand.
    * @param cardsOnTable
    *   the current cards on the table
    * @return
    *   true if the table is empty, false otherwise
    */
  def freeStart(using cardsOnTable: List[CardModel]): Boolean =
    cardsOnTable.isEmpty

  /** If the player starts the turn, they can only play the higher rank card in
    * their hand
    *
    * @param cardsOnTable
    *   the current cards on the table
    * @param playerHand
    *   the player's hand
    * @param playedCard
    *   the card being played
    * @return
    *   true if the player played their highest card, false otherwise
    */
  def startWithHigherCard(using
      cardsOnTable: List[CardModel],
      playerHand: DeckModel,
      playedCard: CardModel
  ): Boolean =
    cardsOnTable.isEmpty &&
      !playerHand.view.exists(_.rank > playedCard.rank)

  /** The player must follow the first suit played on the table.
    *
    * @param cardsOnTable
    *   the current cards on the table
    * @param playerHand
    *   the player's hand
    * @param playedCard
    *   the card being played
    * @return
    *   true if the card played matches the first suit on the table or if the
    *   player has no cards of that suit
    */
  def followFirstSuit(using
      cardsOnTable: List[CardModel],
      playerHand: DeckModel,
      playedCard: CardModel
  ): Boolean =
    cardsOnTable.nonEmpty &&
      (cardsOnTable.head.suit == playedCard.suit ||
        !playerHand.view.exists(_.suit == cardsOnTable.head.suit))

  /** The player must follow the previous suit played on the table.
    *
    * @param cardsOnTable
    *   the current cards on the table
    * @param playerHand
    *   the player's hand
    * @param playedCard
    *   the card being played
    * @return
    *   true if the card played matches the previous suit on the table or if the
    *   player has no cards of that suit
    */
  def followPreviousSuit(using
      cardsOnTable: List[CardModel],
      playerHand: DeckModel,
      playedCard: CardModel
  ): Boolean =
    val previousSuit = cardsOnTable.lastOption.map(_.suit)
    cardsOnTable.nonEmpty &&
    (previousSuit.get == playedCard.suit ||
      !playerHand.view.exists(_.suit == cardsOnTable.head.suit))

  /** Marafone ruleset for playing a card. The starting player can play any
    * card, and subsequent players must follow the first suit played in the
    * turn.
    *
    * @param cardsOnTable
    *   the current cards on the table
    * @param playerHand
    *   the player's hand
    * @param playedCard
    *   the card being played
    * @return
    *   true if the player can play according to Marafone rules, false otherwise
    */
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
  /** Represents a rule for deciding who won the turn.
    *
    * The rule is a function that takes the current cards on the table and
    * returns an optional PlayerModel indicating who has won the turn.
    */
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
    /** Combines two PlayRules, where the first rule takes precedence.
      *
      * @param other
      *   the second rule to apply after the first one
      * @return
      *   a new PlayRule that applies sequentially the first rule and then the
      *   second one
      */
    infix def prevailsOn(
        other: List[(PlayerModel, CardModel)] => Option[PlayerModel]
    ): List[(PlayerModel, CardModel)] => Option[PlayerModel] =
      (cardsOnTable: List[(PlayerModel, CardModel)]) =>
        rule(cardsOnTable) orElse other(cardsOnTable)

object WinRule:
  /** Represents a rule for determining the winning team based on player scores.
    *
    * The rule is a function that takes a list of teams and a list of players,
    * and returns the ordered list of teams based on their total scores.
    */
  opaque type WinRule = (List[Team], List[PlayerModel]) => List[Team]

  /** Orders the teams based on the highest total score of their players.
    *
    * @param teams
    *   the list of teams to order
    * @param players
    *   the list of players with their scores
    * @return
    *   a list of teams ordered by their total scores in descending order
    */
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

  /** Orders the teams based on the lowest total score of their players.
    *
    * @param teams
    *   the list of teams to order
    * @param players
    *   the list of players with their scores
    * @return
    *   a list of teams ordered by their total scores in ascending order
    */
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
