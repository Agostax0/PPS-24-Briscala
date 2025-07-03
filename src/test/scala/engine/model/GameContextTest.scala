package engine.model

import dsl.types.HandRule.HandRule
import dsl.types.PlayRule.PlayRule
import dsl.types.PointsRule.PointsRule
import dsl.types.Team.Team
import dsl.types.WinRule.WinRule
import dsl.types.*
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

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
