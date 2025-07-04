package examples

import dsl.syntax.SyntacticSugar.*
import dsl.syntax.SyntacticSugarBuilder
import dsl.syntax.SyntacticSugarBuilder.highest
import dsl.types.HandRule.{followFirstSuit, followPreviousSuit, freeStart, or, startWithHigherCard}
import dsl.types.PlayRule.*
import dsl.types.Team.Team
import dsl.types.WinRule.highest as highestPointTeam
import dsl.types.{HandRule, PlayRule, PointsRule, WinRule}
import dsl.{GameBuilder, GameDSL, SimpleGameBuilder}
import engine.model.BotType.{Random, Smart}
import engine.model.{CardModel, DeckModel, PlayerModel}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class GameExamplesTest
    extends AnyFlatSpec
    with should.Matchers
    with BeforeAndAfterEach:
  import examples.GameExamples.*

  private val wrongClassText = "GameBuilder is not of type SimpleGameBuilder"

  private val playedCard: CardModel = CardModel("4", 4, "Cups")

  override def beforeEach(): Unit = GameDSL(new SimpleGameBuilder())

  "Briscola game" should "be correctly built" in:
    val briscolaGame = briscola()
    val briscolaBuilder: SimpleGameBuilder = SimpleGameBuilder("Briscola")
    briscolaBuilder.setPlayers(4)
    briscolaBuilder.addPlayer("Alice")
    briscolaBuilder.addPlayer("Bob")
    briscolaBuilder.addBotPlayer("Albert", Smart)
    briscolaBuilder.addBotPlayer("Josh", Random)
    briscolaBuilder.setSuits(List("Cups", "Coins", "Swords", "Batons"))
    briscolaBuilder.setRanks(
      List("2", "4", "5", "6", "7", "Knave", "Knight", "King", "3", "Ace"))
    briscolaBuilder.setPlayersHands(3)
    briscolaBuilder.setStartingPlayer("Alice")
    briscolaBuilder.setBriscolaSuit("Cups")
    briscolaBuilder.setPointRule(PointsRule(
      (name: String, suit: String) =>
        name match
          case "Ace" => 11
          case "3"   => 10
          case "King" => 4
          case "Knight" => 3
          case "Knave" => 2
          case _ => 0)
    )
    val highestBriscolaTakesRule = (cards: List[(PlayerModel, CardModel)]) =>
      given List[(PlayerModel, CardModel)] = cards

      highest(suit) that takes is briscolaBuilder.briscola

    val highestCardTakesRule = (cards: List[(PlayerModel, CardModel)]) =>
      given List[(PlayerModel, CardModel)] = cards

      highest(rank) that takes follows first card suit

    briscolaBuilder.setPlayRule(
      PlayRule(highestBriscolaTakesRule.prevailsOn(highestCardTakesRule))
    )
    briscolaBuilder.setWinRule(WinRule(
      (teams, listOfPlayers) =>
        given List[Team] = teams

        given List[PlayerModel] = listOfPlayers

        highestPointTeam
    ))

    briscolaGame match
      case g: GameBuilder => g.equals(briscolaBuilder) shouldBe true
      case _                    => fail(wrongClassText)

  "Marafone game" should "be correctly built" in:
    val marafoneGame = marafone()
    val marafoneBuilder = SimpleGameBuilder("Marafone")
    marafoneBuilder.setPlayers(4)
    marafoneBuilder.addPlayer("Alice")
    marafoneBuilder.addPlayer("Bob")
    marafoneBuilder.addPlayer("Bob1")
    marafoneBuilder.addPlayer("Bob2")
    marafoneBuilder.addTeam(List("Alice", "Bob"))
    marafoneBuilder.addTeam(List("Bob1", "Bob2"))
    marafoneBuilder.setSuits(List("Cups", "Coins", "Swords", "Batons"))
    marafoneBuilder.setRanks(
      List("4", "5", "6", "7", "Knave", "Knight", "King", "Ace", "2", "3"))
    marafoneBuilder.setPlayersHands(10)
    marafoneBuilder.setStartingPlayer("Alice")
    marafoneBuilder.setBriscolaSuit("Cups")
    marafoneBuilder.setPointRule(PointsRule(
      (name: String, suit: String) =>
        name match
          case "Ace" => 10
          case "3" | "2"| "King" | "Knight" | "Knave" => 3
          case _ => 0
    ))
    marafoneBuilder.setHandRule(HandRule(
      (cardsOnTable, playerHand, playedCard) =>
        given List[CardModel] = cardsOnTable
        given DeckModel = playerHand
        given CardModel = playedCard

        freeStart or followFirstSuit
    ))
    val highestBriscolaTakesRule = (cards: List[(PlayerModel, CardModel)]) =>
      given List[(PlayerModel, CardModel)] = cards

      highest(suit) that takes is marafoneBuilder.briscola

    val highestCardTakesRule = (cards: List[(PlayerModel, CardModel)]) =>
      given List[(PlayerModel, CardModel)] = cards

      highest(rank) that takes follows first card suit
    marafoneBuilder.setPlayRule(PlayRule(
      highestBriscolaTakesRule prevailsOn highestCardTakesRule
    ))
    marafoneBuilder.setWinRule(WinRule(
      (teams, listOfPlayers) =>
        given List[Team] = teams

        given List[PlayerModel] = listOfPlayers

        highestPointTeam
    ))
    marafoneGame match
      case g: GameBuilder => g.equals(marafoneBuilder) shouldBe true
      case _ => fail(wrongClassText)

  "Custom game" should "be correctly built" in:
    val customGame = custom()
    val customBuilder = SimpleGameBuilder("Briscala")
    customBuilder.setPlayers(4)
    customBuilder.addPlayer("Alice")
    customBuilder.addBotPlayer("SmartBot", Smart)
    customBuilder.addPlayer("Bob")
    customBuilder.addPlayer("Josh")
    customBuilder.setSuits(List("Cups", "Coins", "Swords", "Batons"))
    customBuilder.setRanks(
      List("Ace", "2", "3", "4", "5", "6", "7", "Knave", "Knight", "King"))
    customBuilder.setPlayersHands(7)
    customBuilder.setStartingPlayer("Alice")
    customBuilder.setBriscolaSuit("Cups")
    customBuilder.setHandRule(HandRule(
      (cardsOnTable, playerHand, playedCard) =>
        given List[CardModel] = cardsOnTable
        given DeckModel = playerHand
        given CardModel = playedCard

        startWithHigherCard or followPreviousSuit
    ))
    customBuilder.setPointRule(PointsRule(
      (name: String, suit: String) =>
        name match
          case "Ace" => 5
          case "King" => 2
          case "Knight" => 2
          case "Knave" => 2
          case _ => 0
    ))
    customBuilder.setPointRule(PointsRule(
      (name, suit) =>
        suit match
          case "Coins" => 1
          case _ => 0
    ))
    val highestBriscolaTakesRule = (cards: List[(PlayerModel, CardModel)]) =>
      given List[(PlayerModel, CardModel)] = cards

      highest(suit) that takes is customBuilder.briscola

    val highestCardTakesRule = (cards: List[(PlayerModel, CardModel)]) =>
      given List[(PlayerModel, CardModel)] = cards

      highest(rank) that takes follows last card suit
    customBuilder.setPlayRule(PlayRule(
      highestBriscolaTakesRule prevailsOn highestCardTakesRule
    ))
    customBuilder.setWinRule(WinRule(
      (teams, listOfPlayers) =>
        given List[Team] = teams

        given List[PlayerModel] = listOfPlayers

        highestPointTeam
    ))

    customGame match
      case g: GameBuilder => g.equals(customBuilder) shouldBe true
      case _                    => fail(wrongClassText)

