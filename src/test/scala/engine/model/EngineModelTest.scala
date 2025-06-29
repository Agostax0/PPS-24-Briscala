package engine.model

import dsl.types.PlayRule.prevailsOn
import dsl.types.{HandRule, PlayRule, PointsRule, Suits}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers.be
import org.scalatest.matchers.should

import scala.language.postfixOps

class EngineModelTest extends AnyFlatSpec with should.Matchers with BeforeAndAfterEach:
  var engine: FullEngineModel = _
  var alice: PlayerModel = PlayerModel("Alice")
  var bob: PlayerModel = PlayerModel("Alice")
  var player1: PlayerModel = PlayerModel("Alice")
  var player2: PlayerModel = PlayerModel("Bob")
  val suits: Suits = Suits(List("Cups", "Coins", "Swords", "Batons"))
  val ranks: List[String] = List("2", "4", "5", "6", "7", "Knave", "Knight", "King", "3", "Ace")
  val pointsRule: PointsRule = PointsRule((name: String, suit: String) => name match {
    case "Ace" => 11
    case "3" => 10
    case "King" => 4
    case "Knight" => 3
    case "Knave" => 2
    case _ => 0
  })
  val handRule: HandRule = HandRule((cardsOnTable: List[CardModel], playerHand: DeckModel, playedCard: CardModel) =>
    cardsOnTable.isEmpty ||
    cardsOnTable.head.suit == playedCard.suit ||
    !playerHand.view.exists(_.suit == cardsOnTable.head.suit)
  )
  val firstPlayerAlwaysWinsRule: PlayRule = PlayRule((cards: List[(PlayerModel, CardModel)]) => Some(cards.head._1))

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

  it should "allow to add points to cards" in:
    engine.createDeck(suits, ranks)
    engine.addPlayers(List(player1, player2))
    engine.giveCardsToPlayers(5)

    val pointRule: PointsRule = PointsRule((name: String, suit: String) => if (name == "Ace") 11 else 0)

    engine.setPointRules(
      List(
        pointRule
      )
    )

  it should "allow computing the turn" in:
    engine.createDeck(suits, List("Ace"))
    engine.addPlayers(List(player1, player2))
    engine.giveCardsToPlayers(1)

    val card1 = player1.hand.view.head
    engine.playCard(player1, card1)
    val card2 = player2.hand.view.head
    engine.playCard(player2, card2)

    engine.setPlayRules(List(firstPlayerAlwaysWinsRule))
    engine.setPointRules(List(pointsRule))

    engine.computeTurn()
    player1.score + player2.score should be > 0

  it should "clear the table after a turn" in :
    engine.createDeck(suits, List("Ace"))
    engine.addPlayers(List(player1, player2))
    engine.giveCardsToPlayers(1)

    val card1 = player1.hand.view.head
    engine.playCard(player1, card1)
    val card2 = player2.hand.view.head
    engine.playCard(player2, card2)

    engine.setPlayRules(List(firstPlayerAlwaysWinsRule))
    engine.setPointRules(List(pointsRule))

    engine.computeTurn()
    engine.cardsOnTable shouldBe empty

  it should "allow to correctly assign winning hand points" in:
    engine.createDeck(suits, List("Ace"))

    engine.addPlayers(List(player1, player2))
    engine.setStartingPlayer(0)

    engine.giveCardsToPlayers(1)

    val card1 = player1.hand.view.head
    engine.playCard(player1, card1)
    val card2 = player2.hand.view.head
    engine.playCard(player2, card2)

    engine.setPlayRules(List(firstPlayerAlwaysWinsRule))
    engine.setPointRules(List(pointsRule))

    engine.computeTurn()
    player1.score shouldBe pointsRule.apply("Ace", "Cups") * 2

  it should "allow to set the briscola suit" in:
    engine.setBriscolaSuit("Cups")
    engine.briscolaSuit shouldBe "Cups"

  it should "allow to set hand rules" in:
    val playOnlySameSuitRule: HandRule = HandRule((cardsOnTable: List[CardModel], playerHand: DeckModel, playedCard: CardModel) =>
      cardsOnTable.isEmpty ||
        cardsOnTable.head.suit == playedCard.suit
    )
    engine.setHandRules(playOnlySameSuitRule)
    engine.createDeck(suits, List("Ace"))

    engine.addPlayers(List(player1, player2))
    engine.setStartingPlayer(0)

    engine.giveCardsToPlayers(1)

    val card1 = player1.hand.view.head
    engine.playCard(player1, card1)
    val card2 = player2.hand.view.head
    engine.playCard(player2, card2) shouldBe false

  it should "allow to set play rules" in:
    engine.setPlayRules(List(firstPlayerAlwaysWinsRule))

  it should "correctly apply the play rule" in:
    //forced the last player to win this turn
    val lastPlayerAlwaysWinsRule: PlayRule = PlayRule((cards: List[(PlayerModel, CardModel)]) => Some(cards.last._1))

    engine.createDeck(suits, List("Ace"))

    engine.addPlayers(List(player1, player2))
    engine.setStartingPlayer(0)

    engine.giveCardsToPlayers(1)

    val card1 = player1.hand.view.head
    engine.playCard(player1, card1)
    val card2 = player2.hand.view.head
    engine.playCard(player2, card2)

    engine.setPointRules(List(pointsRule))
    engine.setPlayRules(List(lastPlayerAlwaysWinsRule))

    engine.computeTurn()
    player2.score shouldBe pointsRule.apply("Ace", "Cups") * 2

  it should "not allow conflicting play rules" in:
    engine.createDeck(suits, List("Ace"))
    engine.addPlayers(List(player1, player2))
    engine.setStartingPlayer(0)
    engine.giveCardsToPlayers(1)
    val card1 = player1.hand.view.head
    engine.playCard(player1, card1)
    val card2 = player2.hand.view.head
    engine.playCard(player2, card2)

    val lastPlayerAlwaysWinsRule: PlayRule = PlayRule((cards: List[(PlayerModel, CardModel)]) => Some(cards.last._1))
    engine.setPlayRules(List(firstPlayerAlwaysWinsRule,lastPlayerAlwaysWinsRule))
    a [IllegalStateException] should be thrownBy engine.computeTurn()

  it should "allow declaration of prevailing play rules" in:
    engine.createDeck(suits, List("Ace"))
    engine.addPlayers(List(alice, bob))
    engine.setStartingPlayer(0)
    engine.giveCardsToPlayers(1)
    val card1 = alice.hand.view.head
    engine.playCard(alice, card1)
    val card2 = bob.hand.view.head
    engine.playCard(bob, card2)
    engine.setPointRules(List(pointsRule))

    val firstPlayerAlwaysWinsRule = ((cards: List[(PlayerModel, CardModel)]) => Some(cards.head._1))
    val lastPlayerAlwaysWinsRule = ((cards: List[(PlayerModel, CardModel)]) => Some(cards.last._1))
    val playRule = lastPlayerAlwaysWinsRule prevailsOn firstPlayerAlwaysWinsRule

    engine.setPlayRules(List(PlayRule(playRule)))

    engine.computeTurn()
    bob.score shouldBe pointsRule.apply("Ace", "Cups") * 2