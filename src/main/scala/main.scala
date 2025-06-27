import dsl.{GameBuilder, GameDSL}
import dsl.GameDSL.*
import dsl.syntax.SyntacticSugar.*
import engine.controller.EngineController

import scala.language.implicitConversions
import scala.language.postfixOps
@main
def main(): Unit =

  game is "Briscala"
  game has 4 players

  game has player called "Alice"
  game has player called "Bob"
  game has player called "Bob1"
  game has player called "Bob2"

  game suitsAre ("Cups", "Coins", "Swords", "Batons")
  game ranksAre ("2", "4", "5", "6", "7", "Knave", "Knight", "King", "3", "Ace")
  game gives 7 cards to every player

  game firstTurn starts from "Bob"

  val rule = (name: String, suit: String) => if (name == "Ace") 11 else 0

  game card points are:
    (name, suit) => name match
        case "Ace" => 11
        case "3" => 10
        case "King" => 4
        case "Knight" => 3
        case "Knave" => 2
        case _ => 0

  EngineController(game.build()).start()

end main
