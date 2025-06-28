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
    cards = cards.updatedWith(playerName) {
      case Some(existingCards) if !existingCards.contains(card) =>
        Some(existingCards :+ card)
      case Some(existingCards) => Some(existingCards)
      case _                   => Some(List(card))
    }

    displayCard(playerName, card)

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

  def removeCardsFromPlayer(playerName: String): State[Frame, Unit] =
    import WindowStateImpl.*
    State(frame =>
      cards(playerName).foreach(card =>
        val componentName = playerName + "_" + card.toString
        removeComponentFromPanel(componentName, playerName)(frame)
      )
      (frame, ())
    )

  private def displayCard(
      playerName: String,
      card: CardModel
  ): State[Frame, Unit] =
    import WindowStateImpl.*
    val cardInfo =
      "<html><body>" + card.name + "<br>" + card.suit + "</body></html>"
    val componentName = playerName + "_" + card.toString
    for
      _ <- addButton(cardInfo, componentName)
      _ <- moveComponentIntoPanel(componentName, playerName)
    yield ()
//  def cardPlayedFromPlayer(playerName: String,):
