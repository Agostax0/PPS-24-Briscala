package engine.model

/** Represents a deck of cards in a card game. Provides methods to manage the
  * deck, including adding, removing, shuffling, and drawing cards.
  */
trait DeckModel:
  /** The list of cards currently in the deck.
    *
    * @return
    *   a view of the current cards in the deck.
    */
  def view: List[CardModel]

  /** Returns the number of cards in the deck.
    *
    * @return
    *   the size of the deck.
    */
  def size(): Int

  /** Adds a card to the deck.
    *
    * @param card
    *   the card to be added.
    */
  def addCard(card: CardModel): Unit

  /** Removes a card from the deck.
    *
    * @param card
    *   the card to be removed.
    * @throws NoSuchElementException
    *   if the card is not found in the deck.
    */
  def removeCard(card: CardModel): Unit

  /** Shuffles the deck using a random number generator.
    *
    * @param random
    *   an implicit random number generator.
    */
  def shuffle()(using random: scala.util.Random): Unit

  /** Draws a specified number of cards from the deck.
    *
    * @param numCards
    *   the number of cards to draw.
    * @return
    *   a list of drawn cards, or an empty list if not enough cards are
    *   available.
    */
  def drawCards(numCards: Int): List[CardModel]

  /** Checks if the deck is empty.
    *
    * @return
    *   true if the deck has no cards, false otherwise.
    */
  def isEmpty: Boolean

  /** Orders the cards in the deck by suit and rank.
   * (to be used when ordering a player's hand)
    */
  def orderHand(): Unit

object DeckModel:
  def apply(): DeckModel = DeckModelImpl()
  given random: scala.util.Random = scala.util.Random()

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
      cards = random.shuffle(cards)

    override def drawCards(numCards: Int): List[CardModel] =
      if cards.isEmpty || cards.size < numCards then List.empty
      else
        val drawnCards = cards.take(numCards)
        cards = cards.drop(numCards)
        drawnCards

    override def isEmpty: Boolean = cards.isEmpty

    override def orderHand(): Unit =
      cards = cards.sortBy(card => (card.suit, card.rank))
