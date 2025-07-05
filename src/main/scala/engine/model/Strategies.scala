package engine.model

import dsl.types.HandRule.HandRule
import dsl.types.PlayRule.PlayRule
import dsl.types.PointsRule.PointsRule
import dsl.types.Suits.Suits
import dsl.types.Team.Team
import dsl.types.WinRule.WinRule
import dsl.types.{HandRule, PlayRule, PointsRule, Suits, Team, WinRule}

/** Strategy for hand rules (which cards can be played from hand)
  */
trait HandRuleStrategy:
  /** Checks if a card can be played based on the current hand rules.
    * @param cardsOnTable
    *   the cards currently on the table
    * @param playerHand
    *   the player's hand of cards
    * @param playedCard
    *   the card that the player wants to play
    * @return
    */
  def canPlayCard(
      cardsOnTable: List[CardModel],
      playerHand: DeckModel,
      playedCard: CardModel
  ): Boolean

/** Default implementation of HandRuleStrategy that allows any card to be
  * played.
  */
class DefaultHandRuleStrategy extends HandRuleStrategy:
  def canPlayCard(
      cardsOnTable: List[CardModel],
      playerHand: DeckModel,
      playedCard: CardModel
  ): Boolean =
    true

/** Custom implementation of HandRuleStrategy that uses a provided hand rule
  * function.
  * @param handRule
  *   the hand rule function that determines if a card can be played
  */
class CustomHandRuleStrategy(handRule: HandRule) extends HandRuleStrategy:
  def canPlayCard(
      cardsOnTable: List[CardModel],
      playerHand: DeckModel,
      playedCard: CardModel
  ): Boolean =
    handRule(cardsOnTable, playerHand, playedCard)

/** Strategy for determining the winning player based on the cards played.
  */
trait PlayRuleStrategy:
  /** Calculates the winning player based on the cards played on the table.
    * @param cardsOnTableByPlayer
    *   the list of cards played on the table, each associated with the player
    *   who played it
    * @return
    *   the winning player, or None if no player wins
    */
  def calculateWinningPlayer(
      cardsOnTableByPlayer: List[(PlayerModel, CardModel)]
  ): Option[PlayerModel]

/** Default implementation of PlayRuleStrategy that determines the winning
  * player based on common briscola rules (highest card of the first suit)
  */
class DefaultPlayRuleStrategy extends PlayRuleStrategy:
  def calculateWinningPlayer(
      cardsOnTableByPlayer: List[(PlayerModel, CardModel)]
  ): Option[PlayerModel] =
    val firstCardPlayed = cardsOnTableByPlayer.head._2
    val playedSuit = firstCardPlayed.suit
    val maxCardByPlayer =
      cardsOnTableByPlayer
        .filter(_._2.suit == playedSuit)
        .max(Ordering.by(_._2.rank))
    val winningPlayer = cardsOnTableByPlayer.find(_.eq(maxCardByPlayer)).get._1
    Some(winningPlayer)

/** Custom implementation of PlayRuleStrategy that uses a list of play
  * @param playRules
  *   the list of play rules to determine the winning player
  */
class CustomPlayRuleStrategy(playRules: List[PlayRule])
    extends PlayRuleStrategy:
  def calculateWinningPlayer(
      cardsOnTableByPlayer: List[(PlayerModel, CardModel)]
  ): Option[PlayerModel] =
    val winningPlayers: List[PlayerModel] =
      playRules
        .map(rule => rule(cardsOnTableByPlayer))
        .filter(_.isDefined)
        .map(_.get)

    if winningPlayers.size > 1 then return None

    val winningPlayer = winningPlayers.head
    Some(winningPlayer)

/** Strategy for calculating points based on the cards played.
  */
trait PointsStrategy:
  /** Calculates the total points based on the cards played.
    * @param cards
    *   the cards played during the turn
    * @return
    *   the total points calculated from the cards
    */
  def calculatePoints(cards: List[CardModel]): Int

/** Default implementation of PointsStrategy that sums the ranks of the cards.
  */
class DefaultPointsStrategy extends PointsStrategy:
  def calculatePoints(cards: List[CardModel]): Int =
    cards.map(card => card.rank).sum // Default implementation sums ranks

/** Custom implementation of PointsStrategy that uses a list of point rules.
  * @param pointRules
  *   the list of point rules to calculate points
  */
class CustomPointsStrategy(pointRules: List[PointsRule]) extends PointsStrategy:
  def calculatePoints(cards: List[CardModel]): Int =
    (for
      card <- cards
      rule <- pointRules
    yield rule(card.name, card.suit)).sum

/** Strategy for determining the winning team based on the game rules.
  */
trait WinRuleStrategy:
  /** Determines the winning team based on the game rules.
    * @param teams
    *   the teams in the game
    * @param listOfPlayers
    *   the list of players in the game
    * @return
    *   the winning team
    */
  def winningGameTeam(
      teams: List[Team],
      listOfPlayers: List[PlayerModel]
  ): Team

/** Default implementation of WinRuleStrategy that uses the highest scoring team
  * as the winner.
  */
class DefaultWinRuleStrategy extends WinRuleStrategy:
  def winningGameTeam(
      teams: List[Team],
      listOfPlayers: List[PlayerModel]
  ): Team =
    val rule = WinRule.highest(using teams, listOfPlayers)
    rule.head

/** Custom implementation of WinRuleStrategy that uses a provided win rule
  * function.
  * @param winRule
  *   the win rule function that determines the winning team
  */
class CustomWinRuleStrategy(winRule: WinRule) extends WinRuleStrategy:
  def winningGameTeam(
      teams: List[Team],
      listOfPlayers: List[PlayerModel]
  ): Team =
    winRule(teams, listOfPlayers).head

/** Trait for managing the deck of cards in the game.
  */
trait DeckManagement:
  /** The deck of cards used in the game.
    */
  var deck: DeckModel = DeckModel()

  /** Gives a specified number of cards to a player from the deck.
    *
    * @param player
    *   the player to whom cards are given
    * @param handSize
    *   the number of cards to give to the player
    */
  def giveCardsToPlayer(player: PlayerModel, handSize: Int): Unit =
    player.drawFromDeck(deck, handSize)

  /** Creates a deck of cards with the specified suits and ranks. It adds a card
    * for each combination of suit and rank to the deck,
    *
    * @param suits
    *   the suits to include in the deck
    * @param ranks
    *   the ranks to include in the deck
    */
  def createDeck(suits: Suits, ranks: List[String]): Unit =
    import DeckModel.given
    for
      rank <- ranks
      suit <- suits
    do deck.addCard(CardModel(rank, ranks.indexOf(rank), suit))
    deck.shuffle()
