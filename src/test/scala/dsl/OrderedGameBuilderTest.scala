package dsl

import dsl.OrderedGameBuilder.OrderedGameBuilderImpl
import dsl.types.{HandRule, PlayRule, PointsRule, WinRule}
import engine.model.BotType.{Random, Smart}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import scala.language.postfixOps

class OrderedGameBuilderTest extends AnyFlatSpec with should.Matchers with BeforeAndAfterEach:

  val emptyString: String = ""

  private def createNonNamedGame: GameBuilder = OrderedGameBuilder(emptyString, GameBuilder(emptyString))

  "A OrderedGameBuilder" should "operate as a normal GameBuilder" in:
    OrderedGameBuilder(emptyString, GameBuilder(emptyString)) shouldBe a [GameBuilder]

  it should "instantiate an empty builder" in:
    createNonNamedGame.simpleEquals(GameBuilder(emptyString))

  "A non-named game" should "not allow to set player count" in:
    a [IllegalStateException] shouldBe thrownBy { createNonNamedGame.setPlayers(4) }

  it should "not allow to set players" in:
    a [IllegalStateException] shouldBe thrownBy {createNonNamedGame.addPlayer("Charlie")}

  it should "not allow to set bot players" in:
    a [IllegalStateException] shouldBe thrownBy {createNonNamedGame.addBotPlayer("Charlie", Smart)}
    a [IllegalStateException] shouldBe thrownBy {createNonNamedGame.addBotPlayer("Charlie", Random)}

  it should "not allow to set suits" in:
    a [IllegalStateException] shouldBe thrownBy {createNonNamedGame.setSuits(List("Cups", "Batons"))}

  it should "not allow to set ranks" in:
    a [IllegalStateException] shouldBe thrownBy {createNonNamedGame.setRanks(List("Ace", "3"))}

  it should "not allow to set hand sizes" in:
    a [IllegalStateException] shouldBe thrownBy {createNonNamedGame.setPlayersHands(1) }

  it should "not allow to add optional configurations" in:

    a [IllegalStateException] shouldBe thrownBy {createNonNamedGame.setStartingPlayer("bob") }
    a [IllegalStateException] shouldBe thrownBy {createNonNamedGame.setPointRule(PointsRule(null)) }
    a [IllegalStateException] shouldBe thrownBy {createNonNamedGame.setPlayRule(PlayRule(null)) }
    a [IllegalStateException] shouldBe thrownBy {createNonNamedGame.setHandRule(HandRule(null)) }
    a [IllegalStateException] shouldBe thrownBy {createNonNamedGame.setWinRule(WinRule(null)) }
    a [IllegalStateException] shouldBe thrownBy {createNonNamedGame.addTeam(List("bob")) }
    a [IllegalStateException] shouldBe thrownBy {createNonNamedGame.setBriscolaSuit("Cups") }

  it should "not build" in:
    a [IllegalStateException] shouldBe thrownBy {createNonNamedGame.build() }


  "A named builder" should "allow to set player count" in:
    val name = "Test"
    val builder = OrderedGameBuilder(name, GameBuilder(name))

    noException shouldBe thrownBy { builder.setPlayers(4) }

