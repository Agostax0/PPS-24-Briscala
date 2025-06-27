package dsl

import dsl.GameDSL.{firstTurn, *}
import dsl.syntax.SyntacticSugar.*
import dsl.types.{HandSize, PlayerCount, Suits}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import scala.language.postfixOps

class GameDSLTest
    extends AnyFlatSpec
    with should.Matchers
    with BeforeAndAfterEach:

  override def beforeEach(): Unit = GameDSL(new SimpleGameBuilder())

  val gameName = "Briscola"
  private val alice = "Alice"
  private val bob = "Bob"

  "a dsl" should "allow to make a game with a name" in:
    game shouldBe a [GameBuilder]

  it should "allow to set the name of the game" in:
    val g = game is gameName

    g.gameName should be(gameName)

  it should "allow to set the number of players" in:
    val g = game has 4 players

    g match
      case g: SimpleGameBuilder => g.playerCount shouldBe PlayerCount(4)

  it should "allow to add players" in:
    val g = game has 2 players

    game has player called alice
    game has player called bob

    g match
      case g: SimpleGameBuilder => g.players should have size 2

  it should "allow to create a deck" in:
    val g = game has 2 players

    game suitsAre ("Cups", "Coins", "Swords", "Batons")
    game ranksAre ("2", "4", "5", "6", "7", "Knave", "Knight", "King", "3", "Ace")
    g match
      case g: SimpleGameBuilder => g.suits.size shouldBe 4
    g match
      case g: SimpleGameBuilder => g.ranks should have size 10

  it should "allow to give cards to players" in:
    val g = game gives 3 cards to every player

    g match
      case g: SimpleGameBuilder => g.handSize shouldBe HandSize(3)


  it should "allow to set the first turn" in:
    val g = game has 2 players

    game has player called alice
    game has player called bob

    game firstTurn starts from alice

    g match
      case g: SimpleGameBuilder =>
        println(g.players.map(_.name))
        println(g.startingPlayerIndex)
        g.startingPlayerIndex shouldBe Some(g.players.map(_.name).indexOf(alice))


  it should "allow to set the first turn correctly" in :
    val g = game has 2 players

    game has player called alice
    game has player called bob

    game firstTurn starts from bob

    g match
      case g: SimpleGameBuilder =>

        g.startingPlayerIndex shouldBe Some((g.players.map(_.name).indexOf(bob)))

  it should "not allow to set the first turn to a non-existent player" in:
    val g = game has 2 players

    game has player called alice
    game has player called bob

    val nonPlayer = "Merk"

    g match
      case g: SimpleGameBuilder =>
        a [IllegalArgumentException] should be thrownBy (game firstTurn starts from nonPlayer)

  it should "not allow to set multiple start turns" in:
    val g = game has 2 players

    game has player called alice
    game has player called bob

    game firstTurn starts from bob

    g match
      case g: SimpleGameBuilder =>
        a [IllegalArgumentException] should be thrownBy (game firstTurn starts from alice)
