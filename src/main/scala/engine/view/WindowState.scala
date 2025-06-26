package engine.view

import engine.view.monads.States.State
import engine.view.monads.Streams.*

trait WindowState:
  type Window
  def initialWindow: Window
  def setSize(width: Int, height: Int): State[Window, Unit]
  def addPanel(panelName: String)(pos: (Int, Int))(
      dims: (Int, Int)
  ): State[Window, Unit]
  def setGridLayout(
      panelName: String,
      layoutOrientation: GridLayoutOrientation
  ): State[Window, Unit]
  def addButton(text: String, name: String): State[Window, Unit]
  def addLabel(text: String, name: String): State[Window, Unit]
  def moveComponentIntoPanel(
      componentName: String,
      panelName: String
  ): State[Window, Unit]
  def show(): State[Window, Unit]
  def exec(cmd: => Unit): State[Window, Unit]
  def eventStream(): State[Window, Stream[String]]

trait GridLayoutOrientation:
  def rows: Int
  def cols: Int
object GridLayoutOrientation:
  case object Vertical extends GridLayoutOrientation:
    override def rows: Int = 1
    override def cols: Int = 0
  case object Horizontal extends GridLayoutOrientation:
    override def rows: Int = 0
    override def cols: Int = 1

object WindowStateImpl extends WindowState:
  import SwingFunctionalFacade.*

  type Window = Frame

  def initialWindow: Window = createFrame

  /** Sets the UI sizes
    * @param width
    *   the width the UI will take
    * @param height
    *   the height the UI will take
    * @return
    */
  def setSize(width: Int, height: Int): State[Window, Unit] =
    State(w => ((w.setSize(width, height)), {}))

  /** Adds a panel to the UI
    * @param panelName
    *   the panel's name for querying purposes
    * @param pos
    *   the position where to put this new panel
    * @param dims
    *   the dimensions that this new panel takes
    * @return
    */
  def addPanel(
      panelName: String
  )(pos: (Int, Int))(dims: (Int, Int)): State[Window, Unit] =
    State(w => (w.addPanel(panelName, pos._1, pos._2, dims._1, dims._2), {}))

  /** Sets a Grid Layout to a specific panel
    * @param panelName
    *   the panel's name for querying purposes
    * @param layout
    *   the panel's configuration
    * @return
    */
  def setGridLayout(
      panelName: String,
      layout: GridLayoutOrientation
  ): State[Window, Unit] =
    State(w => (w.setGridLayout(panelName, layout.rows, layout.cols), {}))

  /** Adds a button to the available components
    * @param text
    *   the text displayed by the button
    * @param name
    *   the button's name for querying purposes
    * @return
    */
  def addButton(text: String, name: String): State[Window, Unit] =
    State(w => ((w.addButton(text, name)), {}))

  /** Adds a label in the UI
    *
    * @param text
    *   the text displayed by the label
    * @param name
    *   the name of the label element for querying purposes
    * @return
    */
  def addLabel(text: String, name: String): State[Window, Unit] =
    State(w => ((w.addLabel(text, name)), {}))

  /** Moves a component inside a panel
    * @param componentName
    *   the component's name
    * @param panelName
    *   the panel's name
    * @return
    */
  def moveComponentIntoPanel(
      componentName: String,
      panelName: String
  ): State[Frame, Unit] =
    State(w => (w.moveComponentIntoPanel(componentName, panelName), {}))
  def show(): State[Window, Unit] =
    State(w => (w.show, {}))
  def exec(cmd: => Unit): State[Window, Unit] =
    State(w => (w, cmd))
  def eventStream(): State[Window, Stream[String]] =
    State(w => (w, Stream.generate(() => w.events().get)))

//@main def windowStateExample =
//  import WindowStateImpl.*
//  import u04.*
//
//  val windowCreation = for
//    _ <- setSize(300, 300)
//    _ <- addButton(text = "inc", name = "IncButton")
//    _ <- addButton(text = "dec", name = "DecButton")
//    _ <- addButton(text = "quit", name = "QuitButton")
//    _ <- addLabel(text = "-", name = "Label1")
//    _ <- show()
//    e <- eventStream()
//  yield e
//
//  val windowEventsHandling = for
//    _ <- windowCreation
//    e <- eventStream()
//    _ <- seqN(e.map(_ match
//      case "IncButton"  => toLabel("i", "Label1")
//      case "DecButton"  => toLabel("d", "Label1")
//      case "QuitButton" => exec(sys.exit())
//    ))
//  yield ()
//
//  windowEventsHandling.run(initialWindow)
