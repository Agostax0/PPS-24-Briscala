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

  it should "correctly follow suit when following the suit with multiple cards on table" in:
    val cardsOnTable = List(
      CardModel("", 7, "Cups"),
      CardModel("", 8, "Batons"),
      CardModel("", 9, "Swords")
    )
    val playerHand = DeckModel()
    playerHand.addCard(CardModel("", 3, "Cups"))
    playerHand.addCard(CardModel("", 4, "Swords"))

    given List[CardModel] = cardsOnTable

    given DeckModel = playerHand

    given CardModel = CardModel("", 3, "Cups")

    HandRule.followFirstSuit shouldBe true


  it should "correctly follow suit when playing different suit with no matching cards" in:
    val cardsOnTable = List(
      CardModel("", 7, "Cups"),
      CardModel("", 8, "Batons")
    )
    val playerHand = DeckModel()
    playerHand.addCard(CardModel("", 3, "Swords"))
    playerHand.addCard(CardModel("", 4, "Batons"))

    given List[CardModel] = cardsOnTable

    given DeckModel = playerHand

    given CardModel = CardModel("", 3, "Swords")

    HandRule.followFirstSuit shouldBe true


  it should "correctly follow suit when not following suit with matching cards available" in :
    val cardsOnTable = List(
      CardModel("", 7, "Cups"),
      CardModel("", 8, "Batons")
    )
    val playerHand = DeckModel()
    playerHand.addCard(CardModel("", 3, "Cups"))
    playerHand.addCard(CardModel("", 4, "Batons"))

    given List[CardModel] = cardsOnTable

    given DeckModel = playerHand

    given CardModel = CardModel("", 4, "Batons")

    HandRule.followFirstSuit shouldBe false


  it should "correctly follow suit when matching last card's suit" in :
    val cardsOnTable = List(
      CardModel("", 7, "Cups"),
      CardModel("", 8, "Batons"),
      CardModel("", 9, "Swords")
    )
    val playerHand = DeckModel()
    playerHand.addCard(CardModel("", 3, "Swords"))
    playerHand.addCard(CardModel("", 4, "Cups"))

    given List[CardModel] = cardsOnTable

    given DeckModel = playerHand

    given CardModel = CardModel("", 3, "Swords")

    HandRule.followPreviousSuit shouldBe true


  it should "correctly follow suit when playing different suit with no matching cards to last suit" in :
    val cardsOnTable = List(
      CardModel("", 7, "Cups"),
      CardModel("", 8, "Swords")
    )
    val playerHand = DeckModel()
    playerHand.addCard(CardModel("", 3, "Cups"))
    playerHand.addCard(CardModel("", 4, "Batons"))

    given List[CardModel] = cardsOnTable

    given DeckModel = playerHand

    given CardModel = CardModel("", 3, "Cups")

    HandRule.followPreviousSuit shouldBe false


  it should "correctly handle single card on table correctly" in :
    val cardsOnTable = List(CardModel("", 7, "Cups"))
    val playerHand = DeckModel()
    playerHand.addCard(CardModel("", 3, "Cups"))

    given List[CardModel] = cardsOnTable

    given DeckModel = playerHand

    given CardModel = CardModel("", 3, "Cups")

    HandRule.followPreviousSuit shouldBe true


  it should "correctly fail when table is empty" in :
    val cardsOnTable = List.empty[CardModel]
    val playerHand = DeckModel()
    playerHand.addCard(CardModel("", 3, "Cups"))

    given List[CardModel] = cardsOnTable

    given DeckModel = playerHand

    given CardModel = CardModel("", 3, "Cups")

    an[NoSuchElementException] should be thrownBy HandRule.followPreviousSuit


  it should "correctly follow suit when not following last suit with matching cards available" in:
    val cardsOnTable = List(
      CardModel("", 7, "Cups"),
      CardModel("", 8, "Swords")
    )
    val playerHand = DeckModel()
    playerHand.addCard(CardModel("", 3, "Swords"))
    playerHand.addCard(CardModel("", 4, "Cups"))

    given List[CardModel] = cardsOnTable

    given DeckModel = playerHand

    given CardModel = CardModel("", 4, "Cups")

    HandRule.followPreviousSuit shouldBe false

