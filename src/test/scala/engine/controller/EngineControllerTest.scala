package engine.controller

import engine.model.{CardModel, FullEngineModel, GameContext}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class ParserTest extends AnyFlatSpec with should.Matchers:

  "EventParser" should "parse valid card played event" in:
    val event = "Alice::Ace 1 Hearts"
    val result = EventParser.parseEvent(event)
    result shouldBe Right(CardPlayedEvent("Alice", CardModel("Ace", 1, "Hearts")))

  it should "return error for invalid event format" in:
    val event = "Alice_Ace 1 Hearts"
    val result = EventParser.parseEvent(event)
    result shouldBe Left("Invalid event format: expected 'playerName::cardInfo', got 'Alice_Ace 1 Hearts'")

  it should "return error for invalid card format" in:
    val event = "Alice::InvalidFormat"
    val result = EventParser.parseEvent(event)
    result shouldBe Left("Invalid card format: expected 'name rank suit', got 'InvalidFormat'")

  it should "return error for invalid rank" in:
    val event = "Alice::Ace InvalidRank Hearts"
    val result = EventParser.parseEvent(event)
    result shouldBe Left("Invalid rank: InvalidRank")

class EngineControllerTest extends AnyFlatSpec with should.Matchers {
  "Engine Controller" should "be correctly built" in:
     EngineController(FullEngineModel("", GameContext())) shouldBe a [EngineController]
}
