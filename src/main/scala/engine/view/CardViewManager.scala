package engine.view

import engine.model.CardModel
import engine.view.SwingFunctionalFacade.Frame
import engine.view.monads.States.State

trait CardViewManager:
  var cards: Map[String, List[CardModel]] = Map.empty
 /**Adds a card to the player's hand in the game view.
  *
  * @param playerName
  *   the name of the player
  * @param card
  *   the card to be added to the player's hand
  * @return
  *   a State that represents the action of adding a card to the player's hand
 * */
  def addCardToPlayer(
      playerName: String,
      card: CardModel,
      isActive: Boolean = true
  ): State[Frame, Unit] =
    cards = cards.updatedWith(playerName) {
      case Some(existingCards) if !existingCards.contains(card) =>
        Some(existingCards :+ card)
      case Some(existingCards) => Some(existingCards)
      case _                   => Some(List(card))
    }

    displayCard(playerName, card, isActive)
/**
  * Removes a specific card from the player's hand in the game view.
  *
  * @param playerName
  *   the name of the player
  * @param card
  *   the card to be removed from the player's hand
  * @return
  *   a State that represents the action of removing a card from the player's hand
 * */
  def removeCardFromPlayer(
      playerName: String,
      card: CardModel
  ): State[Frame, Unit] =
    cards = cards.updatedWith(playerName) { case Some(existingCards) =>
      Some(existingCards.filterNot(_ == card))
      case None => throw new NoSuchElementException("Player Not Found")
    }
    import WindowStateImpl.*
    val componentName = playerName + "::" + card.toString
    for _ <- removeComponentFromPanel(componentName, playerName)
    yield ()
/**
  * Removes all cards from a player's hand in the game view.
  *
  * @param playerName
  *   the name of the player whose cards are to be removed
  * @return
  *   a State that represents the action of removing all cards from the player's hand
* */
  def removeCardsFromPlayer(playerName: String): State[Frame, Unit] =
    import WindowStateImpl.*
    State(frame =>
      cards(playerName).foreach(card =>
        val componentName = playerName + "::" + card.toString
        removeComponentFromPanel(componentName, playerName)(frame)
      )
      (frame, ())
    )
/**
  * Displays a card in the player's hand in the game view.
  *
  * @param playerName
  *   the name of the player
  * @param card
  *   the card to be displayed
  * @return
  *   a State that represents the action of displaying a card in the player's hand
* */
  private def displayCard(
      playerName: String,
      card: CardModel,
      isActive: Boolean
  ): State[Frame, Unit] =
    import WindowStateImpl.*

    val cardInfo =
      "<html><body>" + card.name + "<br>" + card.suit + "</body></html>"
    val componentName = playerName + "::" + card.toString
    if isActive then
      for
        _ <- addButton(cardInfo, componentName)
        _ <- moveComponentIntoPanel(componentName, playerName)
      yield ()
    else
      for
        _ <- addSilencedButton(componentName)
        _ <- moveComponentIntoPanel(componentName, playerName)
      yield ()
