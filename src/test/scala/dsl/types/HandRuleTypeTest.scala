package dsl.types
import dsl.types.HandRule
import dsl.types.HandRule.{HandRule, followFirstSuit, freeStart, marafoneRuleset, startWithHigherCard}
import engine.model.{CardModel, DeckModel, PlayerModel}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import org.scalatest.matchers.should.Matchers.shouldBe

import scala.Console.in
class HandRuleTypeTest
    extends AnyFlatSpec
    with should.Matchers
    with BeforeAndAfterEach:


  var handRule: HandRule = _

  "A hand rule" should "be correctly instantiated" in:
    val rule: (List[CardModel], DeckModel, CardModel) => Boolean = (cards, deck, playedCard) => true

    handRule = HandRule(rule)

  it should "correctly apply logic operands" in:
    val value = true


    import HandRule._
    value.or(false) should be(true)

    value.and(false) should be(false)

  it should "correctly make the marafone ruleset with an empty table" in:
    given List[CardModel] = List.empty
    given DeckModel = DeckModel()
    given CardModel = CardModel("", 0, "")
    marafoneRuleset shouldBe freeStart || followFirstSuit

  it should "correctly make the marafone ruleset with a non-empty table" in:
    given List[CardModel] = List(CardModel("", 0, ""))
    given DeckModel = DeckModel()
    given CardModel = CardModel("", 0, "")
    marafoneRuleset shouldBe false || followFirstSuit

  it should "correctly not start with higher card with a non-empty table" in:
    given List[CardModel] = List(CardModel("", 1, "Cups"), CardModel("", 2, "Batons"))
    val playerHand = DeckModel()
    playerHand.addCard(CardModel("", 3, "Cups"))
    given DeckModel = playerHand
    given CardModel = playerHand.view.head

    startWithHigherCard shouldBe false

  it should "correctly not start with higher card with an empty table" in :

    given List[CardModel] = List.empty

    val playerHand = DeckModel()
    playerHand.addCard(CardModel("", 3, "Cups"))
    playerHand.addCard(CardModel("", 4, "Cups"))

    given DeckModel = playerHand

    given CardModel = playerHand.view.maxBy(_.rank)

    startWithHigherCard shouldBe true

  it should "correctly follow the first suit with a non-empty table" in:
    given List[CardModel] = List(CardModel("", 1, "Cups"), CardModel("", 2, "Batons"))
    val playerHand = DeckModel()
    playerHand.addCard(CardModel("", 3, "Cups"))

    given DeckModel = playerHand
    given CardModel = playerHand.view.head

    followFirstSuit shouldBe true

  it should "correctly follow the first suit with an empty table" in:
    given List[CardModel] = List.empty
    val playerHand = DeckModel()
    playerHand.addCard(CardModel("", 3, "Cups"))

    given DeckModel = playerHand
    given CardModel = playerHand.view.head

    followFirstSuit shouldBe false