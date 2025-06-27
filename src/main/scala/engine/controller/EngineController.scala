package engine.controller

import engine.model.{CardModel, FullEngineModel, PlayerModel}
import engine.view.EngineView
import engine.view.WindowStateImpl.{Window, initialWindow, removeComponentFromPanel}
import engine.view.monads.States.State
import engine.view.ElementsPositionManager.*
import engine.view.monads.Monads.Monad.seqN

sealed trait EngineController:
  def start(): Unit

object EngineController:
  def apply(model: FullEngineModel): EngineController = new EngineControllerImpl(
    model
  )

  private class EngineControllerImpl(private val model: FullEngineModel)
      extends EngineController:
    private val view: EngineView =
      EngineView(model.gameName)(windowWidth, windowHeight)

    private var playerTurn = 0

    override def start(): Unit =
      val initialState =
        for
          _ <- view.addTable()
          _ <- model.players.foldLeft(unitState(): State[Window, Unit]):
            (state, player) =>
              for
                _ <- view.addPlayer(player.name)
                _ <- state
                _ <- player.hand.view.foldLeft(unitState(): State[Window, Unit]):
                  (nestedState, card) =>
                    for
                      _ <- nestedState
                      _ <- view.addCardToPlayer(player.name, card)
                    yield ()
              yield ()
        yield ()

      val windowCreation = initialState.flatMap(_ => view.show)

      val windowEventsHandler: State[Window, Unit] = for
        events <- windowCreation
        _ <- seqN(events.map( event =>
          val parsedEvent = event.split("_")
          val playerName = parsedEvent(0)
          val card = parsedEvent(1).split(" ")
          handleCardPlayed(playerName, card(0), card(1), card(2))
        ))
      yield ()

      windowEventsHandler.run(initialWindow)

    private def handleCardPlayed(playerName: String, name: String , rank: String, suit: String): State[Window, Unit] =
      val card = CardModel(name, rank.toInt, suit)
      println("player "+ playerName +"played"+name)
      model.players.find(playerName == _.name) match
        case Some(player) =>
          if model.canPlayCard(card) && model.players.indexOf(player).eq(playerTurn) then
            for
              _ <- playCard(player, card)
              _ <- view.removeCardFromPlayer(playerName, card)
              _ <- view.addCardToTable(playerName, card)
              _ <- endTurn()
            yield()
          else unitState()
        case _ => throw new NoSuchElementException("Player Not Found")

    private def resetTurn(): State[Window, Unit] =
      playerTurn = 0
      unitState()


    private def drawCard(): State[Window, Unit] =
      try
        model.giveCardsToPlayers(1)
      catch {
        case e: NoSuchElementException => println("Finished cards in the deck")
      }
      for
        _ <- model.players.foldLeft( unitState(): State[Window, Unit]):
          (state, player) =>
            for
              _ <- state
              _ <- view.removeCardsFromPlayer(player.name)
              _ <- player.hand.view.foldLeft(unitState(): State[Window, Unit]):
                (nestedState, card) =>
                  for
                    _ <- nestedState
                    _ <- view.addCardToPlayer(player.name, card)
                  yield ()
            yield()
      yield()

    private def endTurn(): State[Window, Unit] =
      if playerTurn == model.players.size then
        for
          _ <- resetTurn()
          _ <- view.clearTable()
          _ <- drawCard()
          - <- endGame()
        yield()
      else
        unitState()

    private def endGame(): State[Window, Unit] =
      if model.players.forall(_.hand.isEmpty) then
        println("End Game")
      //view.endGame()
      unitState()

    private def playCard(player: PlayerModel, card: CardModel): State[Window, Unit] =
      playerTurn += 1
      model.playCard(player, card)
      unitState()

    private def unitState(): State[Window, Unit] = State(s => (s, ()))
