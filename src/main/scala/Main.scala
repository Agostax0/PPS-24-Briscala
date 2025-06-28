import dsl.{GameBuilder, GameDSL}
import dsl.GameDSL.*
import dsl.syntax.SyntacticSugar.*
import dsl.types.HandRule.*
import dsl.types.PlayRule.{highestCardTakes, highestTrumpTakes, prevails}
import engine.controller.EngineController
import engine.model.{CardModel, DeckModel, PlayerModel}

import scala.language.implicitConversions
import scala.language.postfixOps

def briscola(): GameBuilder =
  game is "Briscala"
  game has 4 players

  game has player called "Alice"
  game has player called "Bob"
  game has player called "Bob1"
  game has player called "Bob2"

  game suitsAre("Cups", "Coins", "Swords", "Batons")
  game ranksAre("2", "4", "5", "6", "7", "Knave", "Knight", "King", "3", "Ace")
  game gives 3 cards to every player

  game firstTurn starts from "Bob"

  game briscolaIs "Cups"

  game card points are :
    (name, suit) =>
      name match
        case "Ace" => 11
        case "3" => 10
        case "King" => 4
        case "Knight" => 3
        case "Knave" => 2
        case _ => 0

  game play rules are :
    ((cards: List[(PlayerModel, CardModel)]) =>
      given List[(PlayerModel, CardModel)] = cards
      given String = "Cups"
      highestTrumpTakes
      ).prevails(
      (cards: List[(PlayerModel, CardModel)]) =>
        given List[(PlayerModel, CardModel)] = cards
        highestCardTakes
    )

  game

def marafone(): GameBuilder =
  game is "Marafone"
  game has 4 players

  game has player called "Alice"
  game has player called "Bob"
  game has player called "Bob1"
  game has player called "Bob2"

  game suitsAre("Cups", "Coins", "Swords", "Batons")
  game ranksAre("4", "5", "6", "7", "Knave", "Knight", "King", "Ace", "2", "3")
  game gives 10 cards to every player

  game firstTurn starts from "Bob"

  game briscolaIs "Cups"

  game card points are:
    (name, suit) =>
      name match
        case "Ace" => 10
        case "3" | "2"| "King" | "Knight" | "Knave" => 3
        case _ => 0

  game hand rules are:
    (cardsOnTable, playerHand, playedCard) =>
      given List[CardModel] = cardsOnTable
      given DeckModel = playerHand
      given CardModel = playedCard

      freeStart or followFirstSuit
      //marafoneRuleset

  game play rules are :
    ((cards: List[(PlayerModel, CardModel)]) =>
      given List[(PlayerModel, CardModel)] = cards
      given String = "Cups"

      highestTrumpTakes
      ).prevails(
      (cards: List[(PlayerModel, CardModel)]) =>
        given List[(PlayerModel, CardModel)] = cards

        highestCardTakes
    )

  game

@main
def main(): Unit =

  val briscolaGame: String = "briscola"
  val marafoneGame: String = "marafone"

  val selectedGame = briscolaGame // oppure "marafone"

  val gameConfig = selectedGame match {
    case game if game == briscolaGame => briscola()
    case game if game == marafoneGame => marafone()
    case _ => throw new IllegalArgumentException(s"Unknown game: $selectedGame")
  }

  EngineController(gameConfig.build()).start()

end main
