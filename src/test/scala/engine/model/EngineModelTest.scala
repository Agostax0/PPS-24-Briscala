package engine.model

import dsl.types.Suits
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class EngineModelTest extends AnyFlatSpec with should.Matchers with BeforeAndAfterEach:
  var engine: FullEngineModel = _
  val player1: PlayerModel = PlayerModel("Alice")
  val player2: PlayerModel = PlayerModel("Bob")
  val suits: Suits = Suits(List("Cups", "Coins", "Swords", "Batons"))
  val ranks: List[String] = List("2", "4", "5", "6", "7", "Knave", "Knight", "King", "3", "Ace")
  
  override def beforeEach(): Unit =
    engine = FullEngineModel("TestGame")
  
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
