import engine.controller.EngineController
import games.Games.*

import scala.language.{implicitConversions, postfixOps}

@main
def main(): Unit =

  val briscolaGame: String = "briscola"
  val marafoneGame: String = "marafone"
  val rovescinoGame: String = "rovescino"
  val customGame: String = "custom"

  val games = List(briscolaGame, marafoneGame, rovescinoGame, customGame)

  Console.out.println(
    "Choose any of these pre-made games: [ " + games.foldRight("")(
      _ + " " + _
    ) + "]"
  )

  val selectedGame = Console.in.readLine()

  val gameConfig = selectedGame match
    case game if game == briscolaGame  => briscola()
    case game if game == marafoneGame  => marafone()
    case game if game == rovescinoGame => rovescino()
    case game if game == customGame    => custom()
    case _ => throw new IllegalArgumentException(s"Unknown game: $selectedGame")

  Console.out.println(
    "Should cards be always visible? [Y / n]"
  )

  val debugger = Console.in.readLine() match
    case "n" | "N" => false
    case _         => true

  EngineController(gameConfig.build(), debugger).start()

end main
