package engine.model

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class EngineModelTest extends AnyFlatSpec with should.Matchers :

  "An EngineModel" should "be correctly instantiated" in:
    val engine = EngineModel("")
    engine.players shouldBe empty

  it should "allow adding players" in:
    val engine = EngineModel("")
    val player1 = PlayerModel("Alice")
    val player2 = PlayerModel("Bob")

    engine.addPlayers(List(player1, player2))
    engine.players should contain theSameElementsAs List(player1, player2)
