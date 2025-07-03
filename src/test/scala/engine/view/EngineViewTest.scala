package engine.view

import engine.model.CardModel
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
  override def beforeEach(): Unit =
    engineView = EngineView("Game")(width, height)

  it should "allow adding card to player" in:
    engineView.addCardToPlayer(player1, card)

    engineView.cards(player1).head shouldBe card

  it should "allow remove card from player" in:
    engineView.addCardToPlayer(player1, card)

    engineView.removeCardFromPlayer(player1, card)

    engineView.cards(player1) shouldBe List.empty

  it should "allow to add a player" in:
    engineView.addPlayer(player1, 3)

    engineView.players.head shouldBe player1

  it should "allow to add a card to a table" in:
    engineView.addTable()

    engineView.addCardToTable(player1, card)

    engineView.cardsPlayed(player1) shouldBe card

}