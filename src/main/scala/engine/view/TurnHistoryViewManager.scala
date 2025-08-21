package engine.view

import dsl.types.Team.Team
import engine.view.ElementsPositionManager.{centerTableDims, historyCoords}
import engine.view.SwingFunctionalFacade.Frame
import engine.view.monads.States.State

trait TurnHistoryViewManager:
  val panelName = "History"

  /** Adds a turn history panel to the game view. This method initializes the
    * panel where turn history will be displayed. It sets up the panel with a
    * specific name and coordinates for its position.
    *
    * @return
    *   a State that represents the action of adding a turn history panel to the
    *   game view
    */
  def addTurnHistory(): State[Frame, Unit] =
    import WindowStateImpl.*
    for _ <- addScrollablePanel(panelName)(historyCoords)(centerTableDims)
    yield ()

  /** Adds a label to the turn history panel indicating the winner of a specific
    * turn. This method updates the turn history panel with the name of the
    * player who won a particular turn.
    *
    * @param playerName
    *   the name of the player who won the turn
    * @param round
    *   the round number in which the player won
    * @return
    *   a State that represents the action of adding a turn winner to the
    *   history panel
    */
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

  /** Declares the overall winner of the game by adding a label to the turn
    * history panel. This method updates the panel with a message indicating the
    * winner of the game.
    *
    * @param playerName
    *   the name of the player who won the game
    * @return
    *   a State that represents the action of declaring the game winner in the
    *   history panel
    */
  def declareWinner(playerName: String): State[Frame, Unit] =
    import WindowStateImpl.*
    for
      _ <- addLabel(
        "THE WINNER IS " + playerName,
        "WINNER " + playerName
      )
      - <- moveComponentIntoPanel(
        "WINNER " + playerName,
        panelName
      )
    yield ()

  /** Prints the scores of all teams in the game to the turn history panel. This
    * method iterates through a list of teams and their corresponding scores,
    * adding a label for each team with its score to the history panel.
    *
    * @param teams
    *   a list of tuples containing each team and its score
    * @return
    *   a State that represents the action of printing team scores to the
    *   history panel
    */
  def printPointsTeam(teams: List[(Team, Int)]): State[Frame, Unit] =
    import WindowStateImpl.*
    teams.foldLeft(State(s => (s, ())): State[Frame, Unit]) {
      (acc, teamWithScore) =>
        val t = teamWithScore._1.reduce((a: String, b: String) =>
          a + " " + b
        ) // or team.mkString(" ")
        acc.flatMap { _ =>
          for
            _ <- addLabel(s"$t score: ${teamWithScore._2}", t)
            _ <- moveComponentIntoPanel(t, panelName)
          yield ()
        }
    }
