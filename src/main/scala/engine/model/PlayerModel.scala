package engine.model

import engine.model.BotType.{Random, Smart}

/** Represents a player in the game. A player has a name, a hand of cards, and a
  * score. Players can play cards and draw from the deck.
  */
sealed trait PlayerModel:
  /** The name of the player. */
  val name: String

  /** The hand of cards the player currently holds. */
  val hand: DeckModel

  /** The score of the player. */
  var score: Int = 0

  /** Plays a card from the player's hand. The card is removed from the hand.
    *
    * @param card
    *   the card to be played
    */
  def playCard(card: CardModel): Unit

  /** Draws a specified number of cards from the deck and adds them to the
    * player's hand.
    *
    * @param deckModel
    *   the deck from which to draw cards
    * @param numCards
    *   the number of cards to draw
    */
  def drawFromDeck(deckModel: DeckModel, numCards: Int): Unit

  /** Increases the player's score by a specified amount.
    *
    * @param score
    *   the amount to increase the score by
    */
  def increaseScore(score: Int): Unit = this.score += score

/** Represents a bot player in the game. It is an extension of a Player that
  * uses a specific decision-making strategy to decide its plays.
  */
sealed trait BotPlayerModel extends PlayerModel:
  /** The BotDecisionStrategy that the bot uses to make decisions.
    */
  val strategy: BotDecisionStrategy

  /** Generates a card based on the bot's strategy and the current game context.
    *
    * @param gameContext
    *   the current game context, including cards on the table and rules
    * @return
    *   a CardModel that the bot has decided to play
    */
  def generateCard(gameContext: GameContext): CardModel

object PlayerModel:
  def apply(name: String): PlayerModel = PlayerModelImpl(name)

  private class PlayerModelImpl(val name: String) extends PlayerModel:
    val hand: DeckModel = DeckModel()

    override def playCard(card: CardModel): Unit =
      hand.removeCard(card)

    override def drawFromDeck(deckModel: DeckModel, numCards: Int): Unit =
      val cards = deckModel.drawCards(numCards)
      cards.foreach(card => hand.addCard(card))
      hand.orderHand()

object BotPlayerModel:
  def apply(name: String, botType: BotType): BotPlayerModel =
    BotPlayerModelImpl(PlayerModel(name), botType)

  private class BotPlayerModelImpl(
      val player: PlayerModel,
      val botType: BotType
  ) extends BotPlayerModel:
    export player.*
    override val strategy: BotDecisionStrategy = botType match
      case Random => RandomDecisionStrategy()
      case Smart  => RuleAwareDecisionStrategy(this)

    override def generateCard(gameContext: GameContext): CardModel =
      strategy.selectCard(hand, gameContext.cardsOnTable, gameContext)
