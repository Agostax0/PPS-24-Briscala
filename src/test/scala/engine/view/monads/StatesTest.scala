package engine.view.monads

import engine.view.monads.States.State
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class StatesTest
  extends AnyFlatSpec
    with should.Matchers
    with BeforeAndAfterEach:

  private var state: State[Int, String] = State(s =>
    (s + 1, s.toString))

  override def beforeEach(): Unit =
    state = State(s => (s + 1, s.toString))

  "States" should "apply a state transformation" in:
    val result = state.run(0)
    result shouldBe (1, "0")

  it should "chain state transformations using flatMap" in:
    val newState = state.flatMap(s => State(s2 =>
      (s2 + 2, s + s2)))
    val result = newState.run(0)
    result shouldBe (3, "01")

