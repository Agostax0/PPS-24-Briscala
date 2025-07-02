package engine.model

import engine.model.BotType.{Random, Smart}

sealed trait PlayerModel:
  val name: String
  val hand: DeckModel
  var score: Int = 0
  def playCard(card: CardModel): Unit
  def drawFromDeck(deckModel: DeckModel, numCards: Int): Unit
  def increaseScore(score: Int): Unit = this.score += score

sealed trait BotPlayerModel extends PlayerModel:
  val strategy: BotDecisionStrategy
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
