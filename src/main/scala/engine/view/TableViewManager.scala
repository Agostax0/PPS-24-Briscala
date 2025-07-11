package engine.view

import engine.model.CardModel
import engine.view.SwingFunctionalFacade.Frame
import engine.view.monads.States.State
import ElementsPositionManager.*
import engine.view.GridLayoutOrientation.{Horizontal, Vertical}

trait TableViewManager:
  var cardsPlayed: Map[String, CardModel] = _
  private var panelName = "Table"
/** * Adds a table to the game view.
 * This method initializes the table where cards will be displayed.
 * It resets the `cardsPlayed` map to an empty state.
 *
 * @return
 *   a State that represents the action of adding a table to the game view
* */
  def addTable(): State[Frame, Unit] =
    cardsPlayed = Map.empty
    import WindowStateImpl.*
    for
      _ <- addPanel(panelName)(centerTableCoords)(centerTableDims)
      _ <- setGridLayout(panelName, Horizontal)
    yield ()

/** * Adds a card to the table for a specific player.
 * This method updates the `cardsPlayed` map with the player's name and the card played.
 * It also adds a label to the table indicating which player played which card.
 *
 * @param playerName
 *   the name of the player who played the card
 * @param card
 *   the card that was played by the player
 * @return
 *   a State that represents the action of adding a card to the table
* */
  def addCardToTable(playerName: String, card: CardModel): State[Frame, Unit] =
    cardsPlayed = cardsPlayed + (playerName -> card)
    val labelName = panelName + " " + playerName
    import WindowStateImpl.*
    for
      _ <- addLabel(
        playerName + ": " + card.name + " of " + card.suit,
        labelName
      )
      - <- moveComponentIntoPanel("Table " + playerName, panelName)
    yield ()
/** * Clears the table by removing all player labels from the table panel.
 * This method iterates through the `cardsPlayed` map and removes each player's label
 * from the table, effectively resetting the table for a new round or game.
 *
 * @return
 *   a State that represents the action of clearing the table
* */
  def clearTable(): State[Frame, Unit] =
    import WindowStateImpl.*
    State(frame =>
      val listPlayers = cardsPlayed.keys.toList
      listPlayers.foreach(playerName =>
        val labelName = panelName + " " + playerName
        removeComponentFromPanel(labelName, panelName)(frame)
      )
      (frame, ())
    )
