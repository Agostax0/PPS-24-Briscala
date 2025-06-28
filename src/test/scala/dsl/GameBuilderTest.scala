package dsl

import dsl.types.{PlayRule, HandRule, PointsRule}
import engine.model.{CardModel, DeckModel, PlayerModel}
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
    builder.addRanks(ranks)

  it should "allow to add suits" in:
    val suits = List("Cups", "Coins", "Swords", "Batons")
    builder.addSuits(suits)

  it should "allow only 4 suits" in:
    val suits = List("Cups", "Coins", "Swords", "Batons", "Stars")
    a [IllegalArgumentException] should be thrownBy builder.addSuits(suits)

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
    builder.addPointRule(pointRule)

  it should "allow to add a briscola suit" in:
    val suits = List("Cups", "Coins", "Swords", "Batons")
    val briscolaSuit = "Cups"
    builder.addSuits(suits)
    builder.addBriscolaSuit(briscolaSuit)

  it should "not allow to add a briscola suit that is not in the list of suits" in:
    val suits = List("Cups", "Coins", "Swords", "Batons")
    val briscolaSuit = "Stars"
    builder.addSuits(suits)
    a [IllegalArgumentException] should be thrownBy builder.addBriscolaSuit(briscolaSuit)

  it should "allow to add a hand rule" in:
    val handRule: HandRule = HandRule((cardsOnTable: List[CardModel], playerHand: DeckModel, playedCard: CardModel) =>
      cardsOnTable.isEmpty ||
        cardsOnTable.head.suit == playedCard.suit
    )
    builder.addHandRule(handRule)

  it should "allow to add a play rule" in:
    val rule: PlayRule = PlayRule((cards: List[(PlayerModel,CardModel)]) => Some(cards.head._1))
    builder.addPlayRule(rule)

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


