package dsl.types

import engine.model.{CardModel, DeckModel, PlayerModel}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import org.scalatest.matchers.should.Matchers.shouldBe

class PlayRuleTypeTest
    extends AnyFlatSpec
    with should.Matchers
    with BeforeAndAfterEach:

  val alice = PlayerModel("Alice")
  val bob = PlayerModel("Bob")

  "A PlayRule" should "prevails on another" in:
    import PlayRule._

    val rule1: List[(PlayerModel, CardModel)] => Option[PlayerModel] = (cards) => Some(alice)
    val rule2: List[(PlayerModel, CardModel)] => Option[PlayerModel] = (cards) => Some(bob)

    rule1.prevailsOn(rule2)(List.empty) should be (Some(alice))

    val rule3: List[(PlayerModel, CardModel)] => Option[PlayerModel] = (cards) => None
    rule3.prevailsOn(rule2)(List.empty) should be(Some(bob))




