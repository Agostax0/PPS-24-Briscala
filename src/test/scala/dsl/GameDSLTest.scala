package dsl

import dsl.GameDSL.*
import dsl.syntax.SyntacticSugar
import dsl.syntax.SyntacticSugar.*
import dsl.syntax.SyntacticSugarBuilder.highest
import dsl.types.PlayRule.prevailsOn
import dsl.types.WinRule.highest as highestPointTeam
import dsl.types.*
import engine.model.{BotPlayerModel, CardModel, DeckModel, PlayerModel}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import dsl.types.HandRule.HandRule
import dsl.types.PlayRule.PlayRule
import dsl.types.PointsRule.PointsRule
import dsl.types.Suits.Suits
import dsl.types.Team.Team
import dsl.types.WinRule.WinRule

import scala.List
import scala.language.{implicitConversions, postfixOps}

class GameDSLTest
    extends AnyFlatSpec
    with should.Matchers
    with BeforeAndAfterEach:

  override def beforeEach(): Unit = GameDSL(new SimpleGameBuilder())

  val gameName = "Briscola"
  private val alice = "Alice"
  private val bob = "Bob"
  private val wrongClassText = "GameBuilder is not of type SimpleGameBuilder"

  "a dsl" should "allow to make a game with a name" in:
    game shouldBe a [GameBuilder]

  it should "allow to set the name of the game" in:
    val g = game is gameName

    g.gameName should be(gameName)

  it should "allow to set the number of players" in:
    val g = game has 4 players

    g match
      case g: SimpleGameBuilder => g.playerCount shouldBe PlayerCount(4)
      case _ => fail(wrongClassText)

  it should "allow to add players" in:
    val g = game has 2 players

    game has player called alice
    game has player called bob

    g match
      case g: SimpleGameBuilder => g.players should have size 2
      case _ => fail(wrongClassText)

  it should "allow to set card suits" in:
    val g = game suitsAre ("Cups", "Coins", "Swords", "Batons")
    g match
      case g: SimpleGameBuilder =>
        g.suits.size shouldBe 4
      case _ => fail(wrongClassText)

  it should "allow to set card ranks" in:
    val g = game ranksAre ("2", "4", "5", "6", "7", "Knave", "Knight", "King", "3", "Ace")
    g match
      case g: SimpleGameBuilder =>
        g.ranks should have size 10
      case _ => fail(wrongClassText)

  it should "allow to give cards to players" in:
    val g = game gives 3 cards to every player

    g match
      case g: SimpleGameBuilder => g.handSize shouldBe HandSize(3)
      case _ => fail(wrongClassText)

  it should "allow to set the first turn" in:
    val g = game has 2 players

    game has player called alice
    game has player called bob

    game firstTurn starts from alice

    g match
      case g: SimpleGameBuilder =>
        println(g.players.map(_.name))
        println(g.startingPlayerIndex)
        g.startingPlayerIndex shouldBe Some(g.players.map(_.name).indexOf(alice))
      case _ => fail(wrongClassText)

  it should "allow to set the first turn correctly" in :
    val g = game has 2 players

    game has player called alice
    game has player called bob

    game firstTurn starts from bob

    g match
      case g: SimpleGameBuilder =>
        g.startingPlayerIndex shouldBe Some(g.players.map(_.name).indexOf(bob))
      case _ => fail(wrongClassText)

  it should "not allow to set the first turn to a non-existent player" in:
    val g = game has 2 players

    game has player called alice
    game has player called bob

    val nonPlayer = "Merk"

    g match
      case g: SimpleGameBuilder =>
        a [IllegalArgumentException] should be thrownBy (game firstTurn starts from nonPlayer)
      case _ => fail(wrongClassText)

  it should "not allow to set multiple start turns" in:
    val g = game has 2 players

    game has player called alice
    game has player called bob

    game firstTurn starts from bob

    g match
      case g: SimpleGameBuilder =>
        a [IllegalArgumentException] should be thrownBy (game firstTurn starts from alice)
      case _ => fail(wrongClassText)

  it should "allow to create point rules" in:
    val rule: (String, String) => Int = (name: String, suit: String) => if (name == "Ace") 11 else 0

    val g = game card points are rule

    import dsl.types.PointsRule
    g match 
      case g: SimpleGameBuilder =>
        g.pointRules.get should contain (PointsRule(rule))
      case _ => fail(wrongClassText)

  it should "allow to set the briscola suit" in:
    val g = game suitsAre ("Cups", "Coins", "Swords", "Batons")
      game briscolaIs "Cups"

    g match
      case g: SimpleGameBuilder => g.briscola shouldBe "Cups"
      case _ => fail(wrongClassText)

  it should "allow to set a hand rule" in:
    val marafoneHandRule: (List[CardModel], DeckModel, CardModel) => Boolean =
      (cardsOnTable, playerHand, playedCard) =>
        cardsOnTable.isEmpty ||
          cardsOnTable.head.suit == playedCard.suit ||
          !playerHand.view.exists(_.suit == cardsOnTable.head.suit)

    val g = game hand rules are marafoneHandRule

    import dsl.types.HandRule
    g match
      case g: SimpleGameBuilder =>
        g.handRule.get should be (HandRule(marafoneHandRule))
      case _ => fail(wrongClassText)

  it should "allow to set a hand rule using advanced syntax" in :
    import dsl.types.HandRule.*
    val marafoneHandRule: (List[CardModel], DeckModel, CardModel) => Boolean =
      (cardsOnTable, playerHand, playedCard) =>
        given List[CardModel] = cardsOnTable
        given DeckModel = playerHand
        given CardModel = playedCard

        freeStart or
          followFirstSuit

    val g = game hand rules are marafoneHandRule

    import dsl.types.HandRule
    g match
      case g: SimpleGameBuilder =>
        g.handRule.get should be(HandRule(marafoneHandRule))
      case _ => fail(wrongClassText)

  it should "allow to not set a hand rule" in:
    val g = game has 2 players

    g match
      case g: SimpleGameBuilder =>
        g.handRule shouldBe None
      case _ => fail(wrongClassText)

  it should "allow to not set a play rule" in :
    val g = game has 2 players

    g match
      case g: SimpleGameBuilder =>
        g.playRules shouldBe None
      case _ => fail(wrongClassText)

  it should "allow to not set a win rule" in :
    val g = game has 2 players

    g match
      case g: SimpleGameBuilder =>
        g.winRule shouldBe None
      case _ => fail(wrongClassText)

  it should "allow to not set a point rule" in :
    val g = game has 2 players

    g match
      case g: SimpleGameBuilder =>
        g.pointRules shouldBe None
      case _ => fail(wrongClassText)

  it should "allow to create a rule for choosing the player who wins a round" in:


    game play rules are:
      ((cards: List[(PlayerModel, CardModel)]) => Some(cards.head._1)) prevailsOn
      ((cards: List[(PlayerModel, CardModel)]) => Some(cards.last._1))


  it should "allow to create a play rule using advanced syntax" in:

    val highestBriscolaTakesRule = (cards: List[(PlayerModel, CardModel)]) =>
      given List[(PlayerModel, CardModel)] = cards
      highest(suit) that takes is briscolaSuit

    val highestCardTakesRule = (cards: List[(PlayerModel, CardModel)]) =>
      given List[(PlayerModel, CardModel)] = cards
      highest(rank) that takes follows first card suit

    val g = game play rules are:
               highestBriscolaTakesRule prevailsOn highestCardTakesRule
    g match
        case g: SimpleGameBuilder =>
          g.playRules.get should have size 1
        case _ => fail(wrongClassText)

  it should "have the correct effect when creating a briscola rule" in:
    game suitsAre("Cups", "Coins", "Swords", "Batons")
    game ranksAre("2", "4", "5", "6", "7", "Knave", "Knight", "King", "3", "Ace")
    game briscolaIs("Cups")

    val aliceP = PlayerModel(alice)
    val bobP = PlayerModel(bob)

    val mockTable = List((aliceP, CardModel("Ace", 11, "Cups")), (bobP, CardModel("Ace", 11, "Batons")))

    val highestFilter = highest(suit)
    val highestFilterSugar = highestFilter that takes
    given List[(PlayerModel, CardModel)] = mockTable
    val winnerChooser = highestFilterSugar is "Cups"

    winnerChooser shouldBe Some(aliceP)

  it should "correctly give errors when a play rule is built incorrectly" in:
    val correctFilterStart = highest(rank)
    val incorrectFilterStart = highest(suit)

    val correctFilter = correctFilterStart that takes
    val incorrectFilter = incorrectFilterStart that takes

    val aliceP = PlayerModel(alice)
    val bobP = PlayerModel(bob)
    val mockTable = List((aliceP, CardModel("Ace", 11, "Cups")), (bobP, CardModel("Ace", 11, "Batons")))

    given List[(PlayerModel, CardModel)] = mockTable
    a [Exception] should be thrownBy (incorrectFilter follows first card suit)
    noException should be thrownBy (correctFilter follows first card suit)
  
  it should "allow to create a play rule using card position and rank as a parameter" in :
    val firstCardSuit = (cards: List[(PlayerModel, CardModel)]) =>
      given List[(PlayerModel, CardModel)] = cards

      highest(rank) that takes follows first card suit

    val lastCardSuit = (cards: List[(PlayerModel, CardModel)]) =>
      given List[(PlayerModel, CardModel)] = cards

      highest(rank) that takes follows last card suit

    val aliceP = PlayerModel(alice)
    val bobP = PlayerModel(bob)
    val card1 = CardModel("Nine", 9, "Cups")
    val card2 = CardModel("10", 10, "Batons")
    val mockTable = List((aliceP, card1), (bobP, card2))

    firstCardSuit(mockTable) should be(Some(aliceP))
    lastCardSuit(mockTable) should be(Some(bobP))

  it should "allow to create vary play rules with a given rank and suit as parameters" in:
    val highestRankOfFirstCardSuit = (cards: List[(PlayerModel, CardModel)]) =>
      given List[(PlayerModel, CardModel)] = cards
      highest(rank) that takes follows first card suit

    val highestRankOfFirstCardRank = (cards: List[(PlayerModel, CardModel)]) =>
      given List[(PlayerModel, CardModel)] = cards
      highest(rank) that takes follows first card rank

    val highestRankOfLastCardSuit = (cards: List[(PlayerModel, CardModel)]) =>
      given List[(PlayerModel, CardModel)] = cards
      highest(rank) that takes follows last card suit

    val highestRankOfLastCardRank = (cards: List[(PlayerModel, CardModel)]) =>
      given List[(PlayerModel, CardModel)] = cards
      highest(rank) that takes follows last card rank

    val aliceP = PlayerModel(alice)
    val bobP = PlayerModel(bob)
    val card1 = CardModel("Nine", 9, "Cups")
    val card2 = CardModel("10", 10, "Batons")
    val mockTable = List((aliceP, card1), (bobP, card2))

    a [IllegalArgumentException] should be thrownBy (
      (cards: List[(PlayerModel, CardModel)]) =>
        given List[(PlayerModel, CardModel)] = cards
        highest(suit) that takes follows last card rank
      )(mockTable)

  it should "allow to set teams for existing players" in :
    val g = game has 2 players

    game has player called alice
    game has player called bob

    game has team composedOf (alice, bob)

    g match
      case g: SimpleGameBuilder => g.teams should have size 1
      case _ => fail(wrongClassText)

  it should "not allow to set teams for non-existing players" in :
    val g = game has 2 players

    game has player called alice
    game has player called bob

    g match
      case g: SimpleGameBuilder =>
        a [IllegalArgumentException] should be thrownBy (game has team composedOf(alice, "Charlie"))
      case _ => fail(wrongClassText)

  it should "not allow to set teams for already teamed up players" in :
    val g = game has 2 players

    game has player called alice
    game has player called bob
    game has player called "Charlie"

    game has team composedOf(alice, bob)

    g match
      case g: SimpleGameBuilder =>
        a [IllegalArgumentException] should be thrownBy (game has team composedOf(alice, "Charlie"))
      case _ => fail(wrongClassText)

  it should "allow to set a win rule" in :
    val winRule:(List[Team], List[PlayerModel]) => List[Team] =
      (team, players)=> team

    val g = game win rules is winRule

    g match
      case g: SimpleGameBuilder =>
        g.winRule.get should be(WinRule(winRule))
      case _ => fail(wrongClassText)

  it should "allow to set a win rule using advanced syntax" in :
    val winRule: (List[Team], List[PlayerModel]) => List[Team] =
      (teams, listOfPlayers) =>
        given List[Team] = teams
        given List[PlayerModel] = listOfPlayers
        highestPointTeam

    val g = game win rules is winRule

    g match
      case g: SimpleGameBuilder =>
        g.winRule.get should be(WinRule(winRule))
      case _ => fail(wrongClassText)

  it should "allow to add bot players" in:
    val g = game has 2 players

    game has smartBot called alice
    game has randomBot called bob

    g match
      case g: SimpleGameBuilder =>
        g.players should have size 2
        g.players shouldBe a [List[BotPlayerModel]]
      case _ => fail(wrongClassText)

  it should "not allow to build a game without enough parameters" in:
    //since it is a SimpleGameBuilder
    noException should be thrownBy game.build()

    GameDSL(null)
    game is "Briscola"

    a [Exception] should be thrownBy game.build()
