package engine.view

object ElementsPositionManager:

  val windowWidth = 1000
  val windowHeight = 1000

  private val widthCender: Int = windowWidth / 2
  private val heightCenter: Int = windowHeight / 2

  val widthPadding = windowWidth / 12
  val heightPadding = windowHeight / 12

  private val sizeTenth = windowWidth / 10

  private val cardHeigth: Int = sizeTenth * 2
  private val playerWidth: Int = sizeTenth * 6

  val horizontalPlayerDims = (cardHeigth, playerWidth)
  val verticalPlayerDims = (playerWidth, cardHeigth)

  val centerPlayerCoords = (widthCender, heightCenter)

  val upPlayerCoords = (cardHeigth, 0)

  val rightPlayerCoords =
    (0, cardHeigth)

  val downPlayerCoords = (
    cardHeigth,
    windowHeight - (cardHeigth + sizeTenth / 2)
  )

  val leftPlayerCoords = (
    windowWidth - cardHeigth,
    cardHeigth
  )
