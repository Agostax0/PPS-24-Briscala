package engine.model

import dsl.types.*
import dsl.types.PlayRule.prevailsOn
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers.be
import org.scalatest.matchers.should

import scala.language.postfixOps

class GameContextTest extends AnyFlatSpec with should.Matchers with BeforeAndAfterEach:
  var gameContext: GameContext = _
  val player1: PlayerModel = PlayerModel("Alice")
  val player2: PlayerModel = PlayerModel("Bob")
  val teams: List[Team] = List(Team(List(player1.name)), Team(List(player2.name)))
  val card1: CardModel = CardModel("Ace", 11, "Cups")
  val card2: CardModel = CardModel("3", 10, "Coins")
  val playerHand: DeckModel = DeckModel()

  override def beforeEach(): Unit =
    gameContext = GameContext()
    playerHand.addCard(card1)

  "A GameContext" should "be correctly instantiated" in:
    gameContext.cardsOnTable shouldBe empty
    gameContext.briscolaSuit shouldBe ""

  it should "allow setting the briscola suit" in:
    gameContext.setBriscolaSuit("Cups")
    gameContext.briscolaSuit shouldBe "Cups"

  it should "allow adding a card to the table" in:
    val player = PlayerModel("Alice")
    val card = CardModel("Ace", 11, "Cups")
    gameContext.addCardToTable(player, card)
    gameContext.cardsOnTable should contain((player, card))

  it should "allow clearing the table" in:
    gameContext.addCardToTable(player1, card1)
    gameContext.clearTable()
    gameContext.cardsOnTable shouldBe empty

  it should "follow default hand rule" in:
    gameContext.canPlayCard(playerHand, card1) shouldBe true

  it should "follow a custom hand rule" in:
    val customHandRule: HandRule = HandRule((cardsOnTable, playerHand, playedCard) =>
      playedCard.suit != "Cups"
    )
    gameContext.setHandRule(customHandRule)

    gameContext.canPlayCard(playerHand, card1) shouldBe false

  it should "follow default play rule" in:
    gameContext.addCardToTable(player1, card1)
    gameContext.addCardToTable(player2, card2)

    gameContext.calculateTurn() shouldBe Some(player1)

  it should "follow a custom play rule" in:
    val customPlayRule: PlayRule = PlayRule(cardsByPlayer =>
      Some(cardsByPlayer.minBy(_._2.rank)._1)
    )
    gameContext.setPlayRules(List(customPlayRule))

    gameContext.addCardToTable(player1, card1)
    gameContext.addCardToTable(player2, card2)

    gameContext.calculateTurn() shouldBe Some(player2)

  it should "follow default points rule" in:
    gameContext.addCardToTable(player1, card1)
    gameContext.addCardToTable(player2, card2)

    gameContext.calculatePoints() shouldBe 21

  it should "follow a custom points rule" in:
    val customPointsRule: PointsRule = PointsRule((name, suit) =>
      if (name == "Ace") 11 else 0
    )
    gameContext.setPointRules(List(customPointsRule))

    gameContext.addCardToTable(player1, card1)
    gameContext.addCardToTable(player2, card2)

    gameContext.calculatePoints() shouldBe 11

  it should "follow default win rule" in:
    player1.score = 10
    player2.score = 5

    val winningTeam = gameContext.calculateWinner(teams, List(player1, player2))
    winningTeam should be (Team(List(player1.name)))

  it should "follow a custom win rule" in:
    player1.score = 10
    player2.score = 5

    val winRule: WinRule = WinRule((teams: List[Team], players: List[PlayerModel]) =>
      val firstPlayerName = players.minBy(p => p.score).name
      val secondPlayerName = players.map(p => p.name).filter(name => !name.equals(firstPlayerName)).head
      List(Team(List(firstPlayerName)), Team(List(secondPlayerName)))
    )

    gameContext.setWinRule(winRule)

    val winningTeam = gameContext.calculateWinner(teams, List(player1, player2))
    winningTeam should be(Team(List(player2.name)))

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

  it should "allow adding teams" in :
    engine.addTeams(List(Team(List(player1.name, player2.name))))
    engine.teams should contain theSameElementsAs List(Team(List(player1.name, player2.name)))

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
    engine.playCard(player1, card) should be (true)
    player1.hand should have size 4

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

  it should "allow to set hand rules" in:
    val playOnlySameSuitRule: HandRule = HandRule((cardsOnTable: List[CardModel], playerHand: DeckModel, playedCard: CardModel) =>
      cardsOnTable.isEmpty ||
        cardsOnTable.head.suit == playedCard.suit
    )
    engine.setHandRule(playOnlySameSuitRule)
    engine.createDeck(suits, List("Ace"))

    engine.addPlayers(List(player1, player2))
    engine.setStartingPlayer(0)

    engine.giveCardsToPlayers(1)

    val card1 = player1.hand.view.head
    engine.playCard(player1, card1)
    val card2 = player2.hand.view.head
    engine.playCard(player2, card2) shouldBe false

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
    val playRule = lastPlayerAlwaysWinsRule  prevailsOn firstPlayerAlwaysWinsRule

    engine.setPlayRules(List(PlayRule(playRule)))

    engine.computeTurn()
    bob.score shouldBe pointsRule.apply("Ace", "Cups") * 2

  it should "allow to set win rule" in:

    val lastPlayerAlwaysWinsRule: PlayRule = PlayRule((cards: List[(PlayerModel, CardModel)]) => Some(cards.last._1))
    val winRule: WinRule = WinRule((teams: List[Team], players:List[PlayerModel])=>
      val firstPlayerName = players.maxBy(p => p.score).name
      val secondPlayerName = players.map(p=>p.name).filter(name=> !name.equals(firstPlayerName)).head
      List(Team(List(firstPlayerName)), Team(List(secondPlayerName)))
    )

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
    engine.setWinRule(winRule)

    engine.computeTurn()
    engine.winningGamePlayers()(0) shouldBe player2.name