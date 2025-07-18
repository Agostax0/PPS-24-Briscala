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

  it should "return an empty list when drawing from an empty deck" in:
    deck.drawCards(1) shouldBe List.empty

  it should "return an empty list when drawing more cards than available" in:
    deck.addCard(card)
    deck.drawCards(2) shouldBe List.empty

  it should "allow removing a card" in:
    deck.addCard(card)
    deck.removeCard(card)
    deck.isEmpty should be(true)

  it should "randomly shuffle cards" in:
    listOfCards.foreach(card => deck.addCard(card))
    val initialCards = deck.view
    given scala.util.Random = scala.util.Random(42) // Fixed seed for reproducibility
    deck.shuffle()
    deck.view should not be initialCards
    deck.view should contain theSameElementsAs listOfCards
