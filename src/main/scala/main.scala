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

  EngineController(game.build()).start()

end main
