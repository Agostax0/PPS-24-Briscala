package engine.model

/** Represents a playing card with a name, rank, and suit.
  */
trait CardModel:
  /** The name of the card (e.g., "Ace", "King", "Queen", etc.).
    */
  def name: String

  /** The rank of the card (e.g., 1 for Ace, 11 for Jack, 12 for Queen, 13 for
    * King).
    */
  def rank: Int

  /** The suit of the card (e.g., "Hearts", "Diamonds", "Clubs", "Spades").
    */
  def suit: String

object CardModel:
  /** Factory method to create a CardModel instance.
    *
    * @param name
    *   The name of the card.
    * @param rank
    *   The rank of the card.
    * @param suit
    *   The suit of the card.
    * @return
    *   A new instance of CardModel.
    */
  def apply(name: String, rank: Int, suit: String): CardModel =
    CardModelImpl(name, rank, suit)

  private final case class CardModelImpl(name: String, rank: Int, suit: String)
      extends CardModel:
    override def toString: String = name + " " + rank + " " + suit
