package engine.view

import engine.model.CardModel
import engine.view.SwingFunctionalFacade.Frame
import engine.view.monads.States.State

trait CardViewManager:
  var cards: Map[String, CardModel] = Map.empty

  def addCardToPlayer(
      playerName: String,
      card: CardModel
  ): State[Frame, Unit] =
    val cardInfo =  card.name + " " + card.suit
    cards = cards + (playerName -> card)
    import WindowStateImpl.*
    val componentName = playerName + "_" + card.toString
    for
      _ <- addButton(cardInfo, componentName)
      _ <- moveComponentIntoPanel(componentName, playerName)
    yield ()

//  def cardPlayedFromPlayer(playerName: String,):
