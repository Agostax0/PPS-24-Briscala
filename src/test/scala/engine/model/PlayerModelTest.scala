package engine.model

import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import scala.language.postfixOps

class PlayerModelTest extends AnyFlatSpec with should.Matchers with BeforeAndAfterEach:
  val playerName = "Bob"
  var player : PlayerModel = _

  override def beforeEach(): Unit =
    player = PlayerModel(playerName)

  "A player" should "be correctly instantiated" in:
    player.name should be(playerName)

  it should " not have instantiated incorrectly" in:
    val incorrectName = "Alice"

    player.name should not be incorrectName
