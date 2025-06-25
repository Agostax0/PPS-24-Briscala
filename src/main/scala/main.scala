import dsl.{GameBuilder, GameDSL}
import dsl.GameDSL.*
import dsl.syntax.SyntacticSugar.*
import engine.controller.EngineController

import scala.language.implicitConversions
import scala.language.postfixOps
@main
def main(): Unit =

  game is "Briscala"
  game has 2 players

  game has player called "Alice"
  game has player called "Bob"
  game suitsAre ("Cups", "Coins", "Swords", "Batons")
  game ranksAre ("2", "4", "5", "6", "7", "Knave", "Knight", "King", "3", "Ace")
  game gives 3 cards to every player

  EngineController(game.build()).start()

end main
