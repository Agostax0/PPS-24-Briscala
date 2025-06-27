package engine.view

import engine.view.ElementsPositionManager.{centerTableDims, historyCoords}
import engine.view.SwingFunctionalFacade.Frame
import engine.view.monads.States.State

trait TurnHistoryViewManager:
  def addTurnHistory(): State[Frame, Unit] =
    import WindowStateImpl.*
    for _ <- addPanel("History")(historyCoords)(centerTableDims)
    yield ()

  def addTurnWinner(playerName: String, round: String): State[Frame, Unit] =
    import WindowStateImpl.*
    for
      _ <- addLabel(
        "Turn " + round + " Winner is " + playerName,
        "History " + playerName + " " + round
      )
      - <- moveComponentIntoPanel(
        "History " + playerName + " " + round,
        "History"
      )
    yield ()

  def declareWinner(playerName: String): State[Frame, Unit] =
    import WindowStateImpl.*
    for
      _ <- addLabel(
        "THE WINNER OF THE GAME IS " + playerName,
        "WINNER " + playerName
      )
      - <- moveComponentIntoPanel(
        "WINNER " + playerName,
        "History"
      )
    yield ()
