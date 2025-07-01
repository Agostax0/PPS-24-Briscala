package engine.model

trait BotDecisionStrategy:

  def selectCard(
      playerHand: DeckModel,
      cardsOnTable: List[(PlayerModel, CardModel)],
      gameContext: GameContext
  ): CardModel

class RandomDecisionStrategy extends BotDecisionStrategy:
  override def selectCard(
      playerHand: DeckModel,
      cardsOnTable: List[(PlayerModel, CardModel)],
      gameContext: GameContext
  ): CardModel = 
    val validCards = for
      card <- playerHand.view if gameContext.canPlayCard(playerHand, card)
    yield (card)
    validCards(scala.util.Random.nextInt(validCards.size))
