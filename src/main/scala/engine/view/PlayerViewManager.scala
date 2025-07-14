package engine.view

import engine.view.ElementsPositionManager.*
import engine.view.SwingFunctionalFacade.Frame
import engine.view.monads.States.State

trait PlayerViewManager:
  var players: List[String] = List.empty

  /** adds a Player to the game view.
    *
    * @param name
    *   the name of the player
    * @param numberOfPlayers
    *   the total number of players in the game
    * @return
    *   a State that represents the action of adding a player to the game view
    */
  def addPlayer(name: String, numberOfPlayers: Int): State[Frame, Unit] =
    players = players :+ name
    import WindowStateImpl.*
    val playerInfo = getPlayerInfo(numberOfPlayers)
    for
      _ <- addPanel(name)(playerInfo._1)(playerInfo._2)
      _ <- setGridLayout(name, playerInfo._3)
    yield ()

  /** * Retrieves the player position configuration based on the number of
    * players.
    *
    * @param numberOfPlayers
    *   the total number of players in the game
    * @return
    *   a tuple containing the coordinates for the player panel, the dimensions
    *   of the panel, and the grid layout orientation for the player panel.
    */
  private def getPlayerInfo(
      numberOfPlayers: Int
  ): ((Int, Int), (Int, Int), GridLayoutOrientation) =
    val config = playerPositionConfigurations(numberOfPlayers)(players.size - 1)

    config
