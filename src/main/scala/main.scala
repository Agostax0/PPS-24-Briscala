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

  EngineController(game.build()).start()

end main
