package engine.view

import engine.model.CardModel
import engine.view.SwingFunctionalFacade.Frame
import engine.view.monads.States.State
import ElementsPositionManager.*

trait TableViewManager:
  private var cardsPlayed: Map[String, CardModel] = _
  def addTable(): State[Frame, Unit] =
    cardsPlayed = Map.empty
    import WindowStateImpl.*
    for
      _ <- addPanel("Table")(widthCenter, heightCenter)(cardHeigth, cardHeigth)
    yield ()
  def addCardToTable(playerName: String, card: CardModel): State[Frame, Unit] =
    cardsPlayed = cardsPlayed + (playerName -> card)
    import WindowStateImpl.*
    for
      _ <- addLabel(
        "Turn " + cardsPlayed.size + " Player: " + playerName + " Card: " + card.name,
        "Table " + playerName
      )
      - <- moveComponentIntoPanel("Table " + playerName, "Table")
    yield ()
