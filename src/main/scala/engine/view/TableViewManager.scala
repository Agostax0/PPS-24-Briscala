package engine.view

import engine.model.CardModel
import engine.view.SwingFunctionalFacade.Frame
import engine.view.monads.States.State
import ElementsPositionManager.*
import engine.view.GridLayoutOrientation.{Horizontal, Vertical}

trait TableViewManager:
  var cardsPlayed: Map[String, CardModel] = _
  private var panelName = "Table"

  def addTable(): State[Frame, Unit] =
    cardsPlayed = Map.empty
    import WindowStateImpl.*
    for
      _ <- addPanel(panelName)(centerTableCoords)(centerTableDims)
      _ <- setGridLayout(panelName, Horizontal)
    yield ()
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
