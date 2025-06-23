package dsl

import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import GameDSL._
class GameDSLTest
    extends AnyFlatSpec
    with should.Matchers
    with BeforeAndAfterEach:

  var dsl: GameBuilder = _
  override def beforeEach(): Unit = GameDSL(GameBuilder(""))

  "a dsl" should "allow to make a game with a name" in:
    game shouldBe a [GameBuilder]