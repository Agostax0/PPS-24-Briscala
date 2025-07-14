package engine.view

import engine.view.SwingFunctionalFacade.Frame
import engine.view.monads.States.State
import engine.view.monads.Streams.*

sealed trait EngineView
    extends PlayerViewManager
    with CardViewManager
    with TableViewManager
    with TurnHistoryViewManager:
  def show: State[Frame, Stream[String]]
  def end(): State[Frame, Unit]

object EngineView:
  def apply(gameName: String)(width: Int, height: Int): EngineView =
    new EngineViewImpl(gameName, width, height)

  private class EngineViewImpl(
      val gameName: String,
      val width: Int,
      val height: Int
  ) extends EngineView:
    def show: State[Frame, Stream[String]] =
      import WindowStateImpl.*
      for
        _ <- setSize(width, height)
        _ <- addTitle(gameName)
        _ <- WindowStateImpl.show()
        e <- eventStream()
      yield e

    def end(): State[Frame, Unit] = State(w => (w, sys.exit()))
