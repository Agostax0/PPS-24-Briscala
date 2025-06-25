package engine.view

import engine.view.SwingFunctionalFacade.Frame
import engine.view.monads.States.State

trait CardViewManager:
  var cards: Map[String, String] = Map.empty

  def addCardToPlayer(
      playerName: String,
      cardRank: String,
      cardSuit: String
  ): State[Frame, Unit] =
    val cardInfo = cardRank + " " + cardSuit
    cards = cards + (playerName -> cardInfo)
    import WindowStateImpl.*
    for
      _ <- addLabel(cardInfo, playerName + "_" + cardInfo)
    yield ()
