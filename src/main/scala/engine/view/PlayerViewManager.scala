package engine.view

import engine.view.ElementsPositionManager.*
import engine.view.SwingFunctionalFacade.Frame
import engine.view.monads.States.State

trait PlayerViewManager:
  var players: List[String] = List.empty

  def addPlayer(name: String): State[Frame, Unit] =
    players = players :+ name
    import WindowStateImpl.*
    val playerInfo = getPlayerInfo
    for
      _ <- addPanel(name)(playerInfo._1)(playerInfo._2)
      _ <- setGridLayout(name, playerInfo._3)
    yield ()

  private def getPlayerInfo: ((Int, Int), (Int, Int), GridLayoutOrientation) =
    import GridLayoutOrientation.*
    players.size match
      case 1 => (downPlayerCoords, horizontalPlayerDims, Vertical)
      case 2 => (upPlayerCoords, verticalPlayerDims, Vertical)
      case 3 => (leftPlayerCoords, verticalPlayerDims, Horizontal)
      case 4 => (rightPlayerCoords, verticalPlayerDims, Horizontal)
