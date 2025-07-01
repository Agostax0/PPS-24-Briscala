package engine.model

sealed trait PlayerModel:
  val name: String
  val hand: DeckModel
  var score: Int = 0
  def playCard(card: CardModel): Unit
  def drawFromDeck(deckModel: DeckModel, numCards: Int): Unit
  def increaseScore(score: Int): Unit = this.score += score

sealed trait BotPlayerModel extends PlayerModel:
  def generateCard(): CardModel

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