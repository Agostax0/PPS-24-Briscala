package engine.model

import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import scala.language.postfixOps

class CardModelTest extends AnyFlatSpec with should.Matchers with BeforeAndAfterEach:
  private val card = CardModel("Ace", 11, "Spades")

  "Card" should "have name 'Ace'" in:
    card.name should be("Ace")

  "Card" should "have rank 11" in:
    card.rank should be(11)

  it should "be of suit Spades" in:
    card.suit should be("Spades")
