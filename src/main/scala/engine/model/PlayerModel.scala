package engine.model

sealed trait PlayerModel:
  val name: String
  val hand: DeckModel
  val score: Int
  def playCard(card: CardModel): Unit
  def drawFromDeck(deckModel: DeckModel, numCards: Int): Unit

object PlayerModel:
  def apply(name: String): PlayerModel = PlayerModelImpl(name)

  private class PlayerModelImpl(val name: String) extends PlayerModel:
    val hand: DeckModel = DeckModel()
    val score: Int = 0

    override def playCard(card: CardModel): Unit =
      hand.removeCard(card)
      
    override def drawFromDeck(deckModel: DeckModel, numCards: Int): Unit =
      val cards = deckModel.drawCards(numCards)
      cards.foreach(
        card => hand.addCard(card)
      )