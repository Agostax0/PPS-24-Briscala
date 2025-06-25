package engine.model

import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import scala.language.postfixOps

class DeckModelTest extends AnyFlatSpec with should.Matchers with BeforeAndAfterEach:
  private var deck: DeckModel = _
  private val card = CardModel("Ace", 11, "Spades")
  private val listOfCards = List(
    card,
    CardModel("Two", 2, "Hearts"),
    CardModel("Three", 3, "Diamonds"),
    CardModel("Four", 4, "Clubs"),
    CardModel("Five", 5, "Spades"),
    CardModel("Six", 6, "Hearts"),
    CardModel("Seven", 7, "Diamonds"),
    CardModel("Eight", 8, "Clubs"),
  )

  override def beforeEach(): Unit =
    deck = DeckModel()

  "Deck" should "be initially empty" in:
    deck.isEmpty should be(true)

  it should "allow adding a card" in:
    deck.addCard(card)
    deck.isEmpty should be(false)

  it should "allow drawing a card" in:
    deck.addCard(card)
    deck.drawCards(1) should be(List(card))
    deck.isEmpty should be(true)

  it should "throw an exception when drawing from an empty deck" in:
    an[NoSuchElementException] should be thrownBy deck.drawCards(1)

  it should "throw an exception when drawing more cards than available" in:
    deck.addCard(card)
    an[NoSuchElementException] should be thrownBy deck.drawCards(2)

  it should "allow removing a card" in:
    deck.addCard(card)
    deck.removeCard(card)
    deck.isEmpty should be(true)

  it should "randomly shuffle cards" in:
    listOfCards.foreach(card => deck.addCard(card))

    given scala.util.Random = scala.util.Random(42) // Fixed seed for reproducibility
    deck.shuffle()
    val drawnCards = deck.drawCards(listOfCards.size)

    drawnCards should not be listOfCards
    drawnCards should contain theSameElementsAs listOfCards
