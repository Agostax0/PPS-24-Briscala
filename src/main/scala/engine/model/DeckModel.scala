package engine.model

trait DeckModel:
  def view: List[CardModel]
  def size(): Int
  def addCard(card: CardModel): Unit
  def removeCard(card: CardModel): Unit
  def shuffle()(random: scala.util.Random): Unit
  def drawCards(numCards: Int): List[CardModel]
  def isEmpty: Boolean

object DeckModel:
  def apply(): DeckModel = DeckModelImpl()

  private final case class DeckModelImpl() extends DeckModel:
    private var cards: List[CardModel] = List.empty

    def view: List[CardModel] = cards.view.toList

    override def size(): Int = cards.size

    override def addCard(card: CardModel): Unit = cards = card :: cards

    override def removeCard(card: CardModel): Unit =
      if cards.contains(card) then cards = cards.filterNot(_ == card)
      else throw new NoSuchElementException("Card not found in the deck")

    given random: scala.util.Random = scala.util.Random()
    override def shuffle()(using random: scala.util.Random): Unit =
      random.shuffle(cards)

    override def drawCards(numCards: Int): List[CardModel] =
      if cards.isEmpty || cards.size < numCards then
        throw new NoSuchElementException("Not enough cards left in the deck")
      else
        val card = cards.take(numCards)
        cards = cards.drop(numCards)
        card

    override def isEmpty: Boolean = cards.isEmpty
