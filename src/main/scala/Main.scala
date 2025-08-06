import engine.controller.EngineController
import games.Games.*

import scala.language.{implicitConversions, postfixOps}

@main
def main(): Unit =

  val briscolaGame: String = "briscola"
  val marafoneGame: String = "marafone"
  val rovescinoGame: String = "rovescino"
  val customGame: String = "custom"

  val selectedGame = rovescinoGame

  val gameConfig = selectedGame match {
    case game if game == briscolaGame => briscola()
    case game if game == marafoneGame => marafone()
    case game if game == rovescinoGame => rovescino()
    case game if game == customGame => custom()
    case _ => throw new IllegalArgumentException(s"Unknown game: $selectedGame")
  }

  EngineController(gameConfig.build()).start()

end main
