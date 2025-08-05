import engine.controller.EngineController
import examples.GameExamples.*

import scala.language.{implicitConversions, postfixOps}

@main
def main(): Unit =

  val briscolaGame: String = "briscola"
  val marafoneGame: String = "marafone"
  val customGame: String = "custom"

  val selectedGame = customGame

  val gameConfig = selectedGame match {
    case game if game == briscolaGame => briscola()
    case game if game == marafoneGame => marafone()
    case game if game == customGame => custom()
    case _ => throw new IllegalArgumentException(s"Unknown game: $selectedGame")
  }

  EngineController(gameConfig.build()).start()

end main
