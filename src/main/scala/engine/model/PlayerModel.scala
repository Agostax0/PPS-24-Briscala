package engine.model

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
  def apply(name: String): BotPlayerModel = BotPlayerModelImpl(name)

  private class BotPlayerModelImpl(val name: String) extends BotPlayerModel:
    val hand: DeckModel = DeckModel()
    override val strategy: BotDecisionStrategy = RuleAwareDecisionStrategy(this)

    override def playCard(card: CardModel): Unit =
      hand.removeCard(card)

    override def drawFromDeck(deckModel: DeckModel, numCards: Int): Unit =
      val cards = deckModel.drawCards(numCards)
      cards.foreach(card => hand.addCard(card))

    override def generateCard(gameContext: GameContext): CardModel =
      strategy.selectCard(hand, gameContext.cardsOnTable, gameContext)
