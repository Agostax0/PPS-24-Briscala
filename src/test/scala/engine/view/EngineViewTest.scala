package engine.view

import engine.model.CardModel
import engine.view.GridLayoutOrientation.{Horizontal, Vertical}
import engine.view.SwingFunctionalFacade.Frame
import engine.view.monads.States.State
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class EngineViewTest extends AnyFlatSpec with should.Matchers with BeforeAndAfterEach{
  var engineView: EngineView = _
  val width = 800
  val height = 800
  val player1 = "Alice"
  val card: CardModel = CardModel("5", 5, "Batons")
  val card1: CardModel = CardModel("4", 4, "Batons")
  val card2: CardModel = CardModel("3", 3, "Batons")
  override def beforeEach(): Unit =
    engineView = EngineView("Game")(width, height)

  it should "allow adding card to player" in:
    engineView.addCardToPlayer(player1, card)

    engineView.cards(player1).head shouldBe card

  it should "allow adding more than one card to player" in :
    engineView.addCardToPlayer(player1, card)

    engineView.addCardToPlayer(player1, card1)

    engineView.cards(player1) shouldBe List(card,card1)

  it should "not allow to add the same card" in:
    engineView.addCardToPlayer(player1, card)

    engineView.addCardToPlayer(player1, card)

    engineView.cards(player1).head shouldBe card

  it should "allow remove card from player" in:
    engineView.addCardToPlayer(player1, card)

    engineView.removeCardFromPlayer(player1, card)

    engineView.cards(player1) shouldBe List.empty

  it should "add multiple cards to the same player" in :
    engineView.addCardToPlayer(player1, card1)
    engineView.addCardToPlayer(player1, card2)

    engineView.cards(player1) should contain allOf(card1, card2)
    engineView.cards(player1) should have size 2

  it should "not add duplicate cards to a player" in :
    engineView.addCardToPlayer(player1, card1)
    engineView.addCardToPlayer(player1, card1)

    engineView.cards(player1) should contain only card1
    engineView.cards(player1) should have size 1

  it should "throw NoSuchElementException when removing card from non-existent player" in :
    a[NoSuchElementException] should be thrownBy
      engineView.removeCardFromPlayer("NonExistentPlayer", card1)


  it should "maintain separate card lists for different players" in :
    val player2 = "Bob"

    engineView.addCardToPlayer(player1, card1)
    engineView.addCardToPlayer(player2, card2)

    engineView.cards(player1) should contain only card1
    engineView.cards(player2) should contain only card2

  it should "not affect other players when removing cards" in :
    val player2 = "Bob"
    engineView.addCardToPlayer(player1, card1)
    engineView.addCardToPlayer(player2, card2)

    engineView.removeCardFromPlayer(player1, card1)

    engineView.cards(player1) shouldBe empty
    engineView.cards(player2) should contain only card2

  it should "maintain card order for a player" in :
    val card3 = CardModel("Queen", 9, "Diamonds")

    engineView.addCardToPlayer(player1, card1)
    engineView.addCardToPlayer(player1, card2)
    engineView.addCardToPlayer(player1, card3)

    engineView.cards(player1) should contain inOrderOnly(card1, card2, card3)

  it should "not allow to remove a card from a non existing player" in:

    a[NoSuchElementException] should be thrownBy engineView.removeCardFromPlayer(player1, card)

  it should "allow to add a player" in:
    engineView.addPlayer(player1, 3)

    engineView.players.head shouldBe player1

  it should "allow to add a card to a table" in:
    engineView.addTable()

    engineView.addCardToTable(player1, card)

    engineView.cardsPlayed(player1) shouldBe card

  "Grid Layout" should "allow to create a Vertical layout" in:
    Vertical.rows shouldBe 1
    Vertical.cols shouldBe 0
  it should "allow to create a Horizontal layout" in:
    Horizontal.rows shouldBe 0
    Horizontal.cols shouldBe 1

}