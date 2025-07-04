package engine.model

import engine.model.BotType.{Random, Smart}
import org.scalatest.{BeforeAndAfterEach, color}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers.oneOf
import org.scalatest.matchers.should

import scala.language.postfixOps

class PlayerModelTest extends AnyFlatSpec with should.Matchers with BeforeAndAfterEach:
  val playerName = "Bob"
  var player : PlayerModel = _

  override def beforeEach(): Unit =
    player = PlayerModel(playerName)

  "A player" should "be correctly instantiated" in:
    player.name should be(playerName)

  it should "not have instantiated incorrectly" in:
    val incorrectName = "Alice"

    player.name should not be incorrectName

  it should "play a card from their hand" in:
    val card = CardModel("Ace", 11, "Spades")
    player.hand.addCard(card)
    player.hand should have size 1

    player.playCard(card)

    player.hand.isEmpty should be(true)

  it should "not play a card that is not in their hand" in:
    val card = CardModel("Ace", 11, "Spades")
    player.hand.addCard(card)
    player.hand should have size 1

    val anotherCard = CardModel("King", 10, "Batons")
    an[NoSuchElementException] should be thrownBy player.playCard(anotherCard)

  it should "draw cards from the deck" in:
    val deck = DeckModel()
    val card1 = CardModel("2", 0, "Cups")
    val card2 = CardModel("3", 1, "Coins")
    deck.addCard(card1)
    deck.addCard(card2)

    player.drawFromDeck(deck, 2)

    player.hand should have size 2
    deck should have size 0

  "A bot" should "be correctly instantiated" in:
    val bot = BotPlayerModel(playerName, Random)
    bot.name shouldBe playerName

  it should "play a card from their hand" in:
    val card = CardModel("Ace", 11, "Spades")
    val bot = BotPlayerModel(playerName, Random)

    bot.hand.addCard(card)
    bot.hand should have size 1

  it should "not play a card that is not in their hand" in :
    val card = CardModel("Ace", 11, "Spades")
    val bot = BotPlayerModel(playerName, Random)
    bot.hand.addCard(card)
    bot.hand should have size 1

    val anotherCard = CardModel("King", 10, "Batons")
    an [NoSuchElementException] should be thrownBy bot.playCard(anotherCard)

  it should "draw cards from the deck" in :
    val deck = DeckModel()
    val card1 = CardModel("2", 0, "Cups")
    val card2 = CardModel("3", 1, "Coins")
    deck.addCard(card1)
    deck.addCard(card2)
    val bot = BotPlayerModel(playerName, Random)

    bot.drawFromDeck(deck, 2)

    bot.hand should have size 2
    deck should have size 0

  "RandomBot" should "choose a card at random among valid cards" in:
      val bot = BotPlayerModel(playerName, Random)
      bot.hand.addCard(CardModel("Ace", 11, "Spades"))
      bot.hand.addCard(CardModel("Ace", 11, "Batons"))
      bot.hand.addCard(CardModel("Ace", 11, "Cups"))


      List("Spades", "Cups", "Batons") should contain (bot.generateCard(GameContext()).suit)

  "SmartBot" should "choose a card among valid cards" in:
    val bot = BotPlayerModel(playerName, Smart)
    bot.hand.addCard(CardModel("Ace", 1, "Spades"))
    bot.hand.addCard(CardModel("Ace", 2, "Batons"))
    bot.hand.addCard(CardModel("Ace", 3, "Cups"))


    bot.generateCard(GameContext()) shouldBe CardModel("Ace", 1, "Spades")


  it should "choose the highest card among valid cards" in:
    val bot = BotPlayerModel(playerName, Smart)
    bot.hand.addCard(CardModel("Ace", 11, "Spades"))
    bot.hand.addCard(CardModel("Ace", 3, "Spades"))
    bot.hand.addCard(CardModel("Ace", 4, "Spades"))
    bot.hand.addCard(CardModel("Ace", 11, "Cups"))
    bot.hand.addCard(CardModel("Ace", 11, "Cups"))
    bot.hand.addCard(CardModel("Ace", 11, "Batons"))

    val table: List[(PlayerModel, CardModel)] = List(PlayerModel("Alice") -> CardModel("Ace", 2, "Spades"))
    val context = GameContext()
    context.cardsOnTable = table
    bot.generateCard(context) should be(CardModel("Ace", 3, "Spades"))

    bot.generateCard(context) shouldBe bot.hand.view.minBy(_.rank)