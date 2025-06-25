package dsl

import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import GameDSL.*
import dsl.syntax.SyntacticSugar._

import scala.language.postfixOps

class GameDSLTest
    extends AnyFlatSpec
    with should.Matchers
    with BeforeAndAfterEach:

  var dsl: GameBuilder = _
  override def beforeEach(): Unit = GameDSL(GameBuilder(""))

  "a dsl" should "allow to make a game with a name" in:
    game shouldBe a [GameBuilder]

  it should "allow to set the name of the game" in:
    val g = game is "Briscola"

    g.gameName should be("Briscola")

  it should "allow to set the number of players" in:
    val g = game has 4 players

    g shouldBe a [GameBuilder]

  it should "allow to add players" in:
    val g = game has 2 players

    game has player called "Alice"
    game has player called "Bob"

    g shouldBe a [GameBuilder]
    g.build().players should have size 2

  it should "not allow adding a number of players different from expected" in:
    game has 2 players

    game has player called "Alice"
    game has player called "Alice"
    game has player called "Alice"

    a [IllegalArgumentException] should be thrownBy game.build()

  it should "allow to create a deck" in:
    val g = game has 2 players

    game has player called "Alice"
    game has player called "Bob"
    game suitsAre ("Cups", "Coins", "Swords", "Batons")
    game ranksAre ("2", "4", "5", "6", "7", "Knave", "Knight", "King", "3", "Ace")

    g shouldBe a [GameBuilder]
    g.build().deck.size() should be(40)


