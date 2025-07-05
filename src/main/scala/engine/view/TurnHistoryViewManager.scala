package engine.view

import dsl.types.Team.Team
import engine.view.ElementsPositionManager.{centerTableDims, historyCoords}
import engine.view.SwingFunctionalFacade.Frame
import engine.view.monads.States.State

trait TurnHistoryViewManager:
  val panelName = "History"
  def addTurnHistory(): State[Frame, Unit] =
    import WindowStateImpl.*
    for _ <- addScrollablePanel(panelName)(historyCoords)(centerTableDims)
    yield ()

  def addTurnWinner(playerName: String, round: String): State[Frame, Unit] =
    import WindowStateImpl.*
    for
      _ <- addLabel(
        "Turn " + round + " Winner is " + playerName,
        panelName + " " + playerName + " " + round
      )
      - <- moveComponentIntoPanel(
        panelName + " " + playerName + " " + round,
        panelName
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
        panelName
      )
    yield ()

  def printPointsTeam(teams:List[(Team,Int)]): State[Frame, Unit] =
    import WindowStateImpl.*
    teams.foldLeft(State(s => (s, ())): State[Frame, Unit]) { (acc, teamWithScore) =>
      val t = teamWithScore._1.reduce((a:String, b:String)=>a + " " + b)  // or team.mkString(" ")
      acc.flatMap { _ =>
        for {
          _ <- addLabel(s"$t score: ${teamWithScore._2}", t)
          _ <- moveComponentIntoPanel(t, panelName)
        } yield ()
      }
    }
