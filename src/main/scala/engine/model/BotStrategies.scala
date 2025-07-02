package engine.model
import engine.model.BotDecisionStrategy.getValidCards

enum BotType:
  case Random
  case Smart

trait BotDecisionStrategy:

  def selectCard(
      playerHand: DeckModel,
      cardsOnTable: List[(PlayerModel, CardModel)],
      gameContext: GameContext
  ): CardModel
object BotDecisionStrategy:
  def getValidCards(
      playerHand: DeckModel,
      cardsOnTable: List[(PlayerModel, CardModel)],
      gameContext: GameContext
  ): List[CardModel] =
    for card <- playerHand.view if gameContext.canPlayCard(playerHand, card)
    yield card

class RandomDecisionStrategy extends BotDecisionStrategy:
  override def selectCard(
      playerHand: DeckModel,
      cardsOnTable: List[(PlayerModel, CardModel)],
      gameContext: GameContext
  ): CardModel =
    val validCards = getValidCards(playerHand, cardsOnTable, gameContext)
    validCards(scala.util.Random.nextInt(validCards.size))

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
  ): CardModel = playerHand.minBy(_.rank)

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
    ).minByOption(_.rank).getOrElse(validCards.minBy(_.rank))
