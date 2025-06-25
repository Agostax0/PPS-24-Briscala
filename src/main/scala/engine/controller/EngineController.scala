package engine.controller

import engine.model.EngineModel
import engine.view.EngineView
import engine.view.WindowStateImpl.{Window, initialWindow}
import engine.view.monads.States.State
import engine.view.ElementsPositionManager.*

sealed trait EngineController:
  def start(): Unit

object EngineController:
  def apply(model: EngineModel): EngineController = new EngineControllerImpl(
    model
  )

  private class EngineControllerImpl(private val model: EngineModel)
      extends EngineController:
    private val view: EngineView =
      EngineView(model.gameName)(windowWidth, windowHeight)

    override def start(): Unit =
      val initialState =
        for
          _ <- model.players.foldLeft(unitState(): State[Window, Unit]):
            (state, player) =>
              for
                _ <- view.addPlayer(player.name)
                _ <- state
                _ <- player.hand.view.foldLeft(unitState(): State[Window, Unit]):
                  (nestedState, card) =>
                    for
                      _ <- nestedState
                      _ <- view.addCardToPlayer(player.name, card.rank.toString, card.suit)
                    yield ()
              yield ()
        yield ()

      val windowCreation = initialState.flatMap(_ => view.show)

      val windowEventsHandler: State[Window, Unit] = for
        _ <- windowCreation
      yield ()

      windowEventsHandler.run(initialWindow)



  private def unitState(): State[Window, Unit] = State(s => (s, ()))
