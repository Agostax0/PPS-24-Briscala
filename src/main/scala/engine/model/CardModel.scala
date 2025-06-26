package engine.model

trait CardModel:
  def name: String
  def rank: Int
  def suit: String

object CardModel:
    def apply(name: String, rank: Int, suit: String): CardModel =
      CardModelImpl(name, rank, suit)

    private final case class CardModelImpl(name: String, rank: Int, suit: String) extends CardModel:
      override def toString: String = name + " " + rank + " " + suit