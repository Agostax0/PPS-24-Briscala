package engine.model
import engine.model.BotDecisionStrategy.getValidCards

/** * BotType represents the type of bot that can be used in the game. It can be
  * either a Random bot or a Smart bot.
  */
enum BotType:
  case Random
  case Smart

/** * BotDecisionStrategy is a trait that defines the strategy for selecting a
  * card from the bot's hand based on the current game context and cards on the
  * table.
  */
trait BotDecisionStrategy:

  /** The bot selects a card to play from its hand based on the current game
    * context
    *
    * @param playerHand
    *   The hand of the bot.
    * @param cardsOnTable
    *   The current cards on the table.
    * @param gameContext
    *   The context of the game, including rules and state.
    * @return
    *   The CardModel selected by the bot.
    */
  def selectCard(
      playerHand: DeckModel,
      cardsOnTable: List[(PlayerModel, CardModel)],
      gameContext: GameContext
  ): CardModel
object BotDecisionStrategy:
  /** Filters the bot's hand to return only the cards that can be played
    * according to the game context.
    *
    * @param playerHand
    *   The hand of the bot.
    * @param cardsOnTable
    *   The current cards on the table.
    * @param gameContext
    *   The context of the game, including rules and state.
    * @return
    *   A list of valid CardModel that can be played.
    */
  def getValidCards(
      playerHand: DeckModel,
      cardsOnTable: List[(PlayerModel, CardModel)],
      gameContext: GameContext
  ): List[CardModel] =
    for card <- playerHand.view if gameContext.canPlayCard(playerHand, card)
    yield card

/** Selects a card randomly from the valid cards in the bot's hand.
  */
class RandomDecisionStrategy extends BotDecisionStrategy:
  override def selectCard(
      playerHand: DeckModel,
      cardsOnTable: List[(PlayerModel, CardModel)],
      gameContext: GameContext
  ): CardModel =
    val validCards = getValidCards(playerHand, cardsOnTable, gameContext)
    validCards(scala.util.Random.nextInt(validCards.size))

/** Selects a card based on the play rules of the game, aiming to win the current
 * turn if possible. If no winning card is available, it selects the card
 * with the lowest point.
 * @param self the PlayerModel representing the bot itself
 */
class RuleAwareDecisionStrategy(private val self: PlayerModel)
    extends BotDecisionStrategy:
  override def selectCard(
      playerHand: DeckModel,
      cardsOnTable: List[(PlayerModel, CardModel)],
      gameContext: GameContext
  ): CardModel =
    val validCards = getValidCards(playerHand, cardsOnTable, gameContext)

    if cardsOnTable.isEmpty then selectOpeningCard(validCards, gameContext)
    else selectWinningCard(validCards, cardsOnTable, gameContext)

  private def selectOpeningCard(
      playerHand: List[CardModel],
      context: GameContext
  ): CardModel = playerHand.minBy(card => context.calculatePoints(List((self, card))))

  private def selectWinningCard(
      validCards: List[CardModel],
      cardsOnTable: List[(PlayerModel, CardModel)],
      context: GameContext
  ): CardModel =
    val expectedWinner = (card: CardModel) =>
      context.calculateTurn(cardsOnTable :+ (self, card))
    (
      for
        playableCard <- validCards
        if expectedWinner(playableCard).get == self
      yield playableCard
    ).minByOption(card => context.calculatePoints(cardsOnTable :+ (self, card)))
      .getOrElse(
        validCards.minBy(card =>
          context.calculatePoints(cardsOnTable :+ (self, card))
        )
      )
