package dsl

import dsl.types.HandRule.HandRule
import dsl.types.PlayRule.PlayRule
import dsl.types.PointsRule.PointsRule
import dsl.types.Team.Team
import dsl.types.WinRule.WinRule
import dsl.types.{HandRule, PlayRule, PointsRule, Team, WinRule}
import engine.model.BotType.{Random, Smart}
import engine.model.{CardModel, DeckModel, FullEngineModel, PlayerModel}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers.be
import org.scalatest.matchers.should

import scala.language.postfixOps

class GameBuilderTest
    extends AnyFlatSpec
      with should.Matchers
    with BeforeAndAfterEach:
  var builder: GameBuilder = _

  var gameName = "Briscola"
  private val alice = "Alice"
  private val bob = "Bob"
  val mark = "Mark"

  override def beforeEach(): Unit =
    builder = GameBuilder(gameName)

  "A game" should "be correctly instantiated" in :
    builder.gameName should be(gameName)

  it should " not have instantiated incorrectly" in :
    val incorrectName = "Marafone"

    builder.gameName should not be incorrectName

  it should "allow a player to join" in:
    val playerName = bob
    builder.addPlayer(playerName)

  it should "allow to add a bot" in:
    val playerName = bob
    builder.addBotPlayer(playerName, Smart)

  it should "allow to add different types of bots" in:
    val bot1Name = bob
    val bot2Name = alice

    builder.addBotPlayer(bot1Name, Smart)
    builder.addBotPlayer(bot2Name, Random)

  it should "not allow a player count not in between 2 or 4" in:
    a [IllegalArgumentException] should be thrownBy builder.setPlayers(7)

  it should "not allow to define N players and then add a number of players different from N" in:
    val n = 4
    builder.setPlayers(n)

    builder.addPlayer(alice)
    builder.addPlayer(bob)
    builder.addPlayer(mark)

    a [IllegalArgumentException] should be thrownBy builder.build()

  it should "allow to add ranks" in:
    val ranks = List("2", "4", "5", "6", "7", "Knave", "Knight", "King", "3", "Ace")
    builder.setRanks(ranks)

  it should "allow to add suits" in:
    val suits = List("Cups", "Coins", "Swords", "Batons")
    builder.setSuits(suits)

  it should "allow only 4 suits" in:
    val suits = List("Cups", "Coins", "Swords", "Batons", "Stars")
    a [IllegalArgumentException] should be thrownBy builder.setSuits(suits)

  it should "start with a black briscola suit" in:
    builder.briscola should be("")

  it should "not allow to set a briscola suit without first setting the game's suits" in:
    val briscolaSuit = "Cups"
    a [Exception] should be thrownBy builder.setBriscolaSuit(briscolaSuit)

  it should "allow to set only valid players hands" in:
    val handSize = 2
    a [IllegalArgumentException] should be thrownBy builder.setPlayersHands(handSize)

  it should "allow to set a starting player" in:
    val startingPlayer = alice
    a [IllegalArgumentException] should be thrownBy builder.setStartingPlayer(startingPlayer)

  it should "not allow to set a starting player that is not in the list of players" in:
    val startingPlayer = bob
    a [IllegalArgumentException] should be thrownBy builder.setStartingPlayer(startingPlayer)

  it should "not allow to set the first turn more than once" in:
    val startingPlayer = alice
    val anotherStartingPlayer = bob

    val n = 4
    builder.setPlayers(n)

    builder.addPlayer(alice)
    builder.addPlayer(bob)

    builder.setStartingPlayer(startingPlayer)
    a [IllegalArgumentException] should be thrownBy builder.setStartingPlayer(anotherStartingPlayer)

  it should "allow to add a point rule" in:
    val pointRule: PointsRule = PointsRule((name: String, suit: String) => if (name == "Ace") 11 else 0)
    builder.setPointRule(pointRule)

  it should "allow to add a briscola suit" in:
    val suits = List("Cups", "Coins", "Swords", "Batons")
    val briscolaSuit = "Cups"
    builder.setSuits(suits)
    builder.setBriscolaSuit(briscolaSuit)

  it should "not allow to add a briscola suit that is not in the list of suits" in:
    val suits = List("Cups", "Coins", "Swords", "Batons")
    val briscolaSuit = "Stars"
    builder.setSuits(suits)
    a [IllegalArgumentException] should be thrownBy builder.setBriscolaSuit(briscolaSuit)

  it should "allow to add a hand rule" in:
    val handRule: HandRule = HandRule((cardsOnTable: List[CardModel], playerHand: DeckModel, playedCard: CardModel) =>
      cardsOnTable.isEmpty ||
        cardsOnTable.head.suit == playedCard.suit
    )
    builder.setHandRule(handRule)

  it should "allow to add a play rule" in:
    val rule: PlayRule = PlayRule((cards: List[(PlayerModel,CardModel)]) => Some(cards.head._1))
    builder.setPlayRule(rule)

  it should "allow a team to be created" in :
    builder.addPlayer(alice)
    builder.addPlayer(bob)
    builder.addTeam(List(alice, bob))

  it should "not allow a team to be created with non existent players" in :
    builder.addPlayer(alice)
    builder.addPlayer(bob)

    a [IllegalArgumentException] should be thrownBy builder.addTeam(List(alice, "Charlie"))

  it should "not allow a team to be created with already teamed up players" in :
    builder.addPlayer(alice)
    builder.addPlayer(bob)
    builder.addPlayer("Charlie")
    builder.addTeam(List(alice, bob))
    a[IllegalArgumentException] should be thrownBy builder.addTeam(List(alice, "Charlie"))

  it should "allow to add a win rule" in :
    val winRule: WinRule = WinRule((teams: List[Team], players: List[PlayerModel]) =>
      teams
    )
    builder.setWinRule(winRule)

  it should "allow to add a type of bot player" in :
    val playerName = bob
    builder.addBotPlayer(playerName, Smart)
    
  it should "allow to build a game when provided all the needed informations" in :
    val ranks = List("2", "4", "5", "6", "7", "Knave", "Knight", "King", "3", "Ace")
    val suits = List("Cups", "Coins", "Swords", "Batons")
    val pointRule: PointsRule = PointsRule((name: String, suit: String) => if (name == "Ace") 11 else 0)
    val playRule: PlayRule = PlayRule((cards: List[(PlayerModel, CardModel)]) => Some(cards.head._1))
    val handRule: HandRule = HandRule((cardsOnTable: List[CardModel], playerHand: DeckModel, playedCard: CardModel) =>
      cardsOnTable.isEmpty || cardsOnTable.head.suit == playedCard.suit)
    val winRule: WinRule = WinRule((teams: List[Team], players: List[PlayerModel]) => teams)

    builder.setPlayers(2)
    builder.addPlayer(alice)
    builder.addPlayer(bob)
    builder.setRanks(ranks)
    builder.setSuits(suits)
    builder.setBriscolaSuit("Cups")
    builder.setPlayersHands(3)
    builder.setStartingPlayer(alice)
    builder.setPointRule(pointRule)
    builder.setPlayRule(playRule)
    builder.setHandRule(handRule)
    builder.setWinRule(winRule)
    builder.addTeam(List(alice))
    builder.addTeam(List(bob))

    noException should be thrownBy builder.build()
    
    val game = builder.build()
    game shouldBe a[FullEngineModel]
    
    
