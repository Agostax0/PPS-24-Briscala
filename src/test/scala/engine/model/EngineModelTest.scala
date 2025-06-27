package engine.model

import dsl.types.Suits
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import scala.language.postfixOps

class EngineModelTest extends AnyFlatSpec with should.Matchers with BeforeAndAfterEach:
  var engine: FullEngineModel = _
  var player1: PlayerModel = PlayerModel("Alice")
  var player2: PlayerModel = PlayerModel("Bob")
  val suits: Suits = Suits(List("Cups", "Coins", "Swords", "Batons"))
  val ranks: List[String] = List("2", "4", "5", "6", "7", "Knave", "Knight", "King", "3", "Ace")
  
  override def beforeEach(): Unit =
    engine = FullEngineModel("TestGame")
    player1 = PlayerModel("Alice")
    player2 = PlayerModel("Bob")

  "An EngineModel" should "be correctly instantiated" in:
    engine.players shouldBe empty

  it should "allow adding players" in:
    engine.addPlayers(List(player1, player2))
    engine.players should contain theSameElementsAs List(player1, player2)
    
  it should "allow creating a deck" in:
    engine.createDeck(suits, ranks)
    engine.deck.size() should be(40)

  it should "allow giving cards to players" in:
    engine.createDeck(suits, ranks)
    engine.addPlayers(List(player1, player2))
    engine.giveCardsToPlayers(5)

    player1.hand should have size 5
    player2.hand should have size 5
    engine.deck should have size 30

  it should "allow players to play a card" in:
    engine.createDeck(suits, ranks)
    engine.addPlayers(List(player1, player2))
    engine.giveCardsToPlayers(5)

    val card = player1.hand.view.head
    engine.playCard(player1, card)

    player1.hand should have size 4
    engine.cardsOnTable should contain (player1 -> card)

  it should "allow shuffling the deck" in:
    engine.createDeck(suits, ranks)
    engine.addPlayers(List(player1, player2))
    engine.giveCardsToPlayers(5)
    given scala.util.Random = scala.util.Random(42)
    val initialDeck = engine.deck.view
    engine.deck.shuffle()

    initialDeck should not be engine.deck.view
    initialDeck should contain theSameElementsAs engine.deck.view

  it should "allow computing the turn" in:
    engine.createDeck(suits, ranks)
    engine.addPlayers(List(player1, player2))
    engine.giveCardsToPlayers(5)

    val card1 = player1.hand.view.head
    engine.playCard(player1, card1)
    val card2 = player2.hand.view.head
    engine.playCard(player2, card2)
    val winningPlayer = if card1.rank > card2.rank then player1 else player2

    engine.computeTurn()
    winningPlayer.score should be (1)
