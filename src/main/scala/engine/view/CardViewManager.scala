package engine.view

import engine.model.CardModel
import engine.view.SwingFunctionalFacade.Frame
import engine.view.monads.States.State

trait CardViewManager:
  var cards: Map[String, List[CardModel]] = Map.empty

  def addCardToPlayer(
      playerName: String,
      card: CardModel
  ): State[Frame, Unit] =
    val cardInfo = card.name + " " + card.suit
    cards = cards.updatedWith(playerName) {
      case Some(existingCards) => Some(existingCards :+ card)
      case _                   => Some(List(card))
    }
    import WindowStateImpl.*
    val componentName = playerName + "_" + card.toString
    for
      _ <- addButton(cardInfo, componentName)
      _ <- moveComponentIntoPanel(componentName, playerName)
    yield ()

  def removeCardFromPlayer(
      playerName: String,
      card: CardModel
  ): State[Frame, Unit] =
    cards = cards.updatedWith(playerName) { case Some(existingCards) =>
      Some(existingCards.filterNot(_ == card))
    }
    import WindowStateImpl.*
    val componentName = playerName + "_" + card.toString
    for _ <- removeComponentFromPanel(componentName, playerName)
    yield ()
//  def cardPlayedFromPlayer(playerName: String,):
