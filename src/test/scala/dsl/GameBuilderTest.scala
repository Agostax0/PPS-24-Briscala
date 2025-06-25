package dsl

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

  override def beforeEach(): Unit =
    builder = GameBuilder(gameName)

  "A game" should "be correctly instantiated" in :
    builder.gameName should be(gameName)

  it should " not have instantiated incorrectly" in :
    val incorrectName = "Marafone"

    builder.gameName should not be incorrectName

  it should "allow a player to join" in:
    val playerName = "Bob"
    builder.addPlayer(playerName)

  it should "not allow a player count not in between 2 or 4" in:
    a [IllegalArgumentException] should be thrownBy builder.setPlayers(7)

  it should "allow to add ranks" in:
    val ranks = List("2", "4", "5", "6", "7", "Knave", "Knight", "King", "3", "Ace")
    builder.addRanks(ranks)

  it should "allow to add suits" in:
    val suits = List("Cups", "Coins", "Swords", "Batons")
    builder.addSuits(suits)

  it should "allow only 4 suits" in:
    val suits = List("Cups", "Coins", "Swords", "Batons", "Stars")
    a [IllegalArgumentException] should be thrownBy builder.addSuits(suits)