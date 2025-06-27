package engine.view

import engine.view.GridLayoutOrientation.{Horizontal, Vertical}

object ElementsPositionManager:

  val windowWidth = 1000
  val windowHeight = 1000

  val widthCenter: Int = windowWidth / 2
  val heightCenter: Int = windowHeight / 2

  val widthPadding = windowWidth / 12
  val heightPadding = windowHeight / 12

  private val sizeTenth = windowWidth / 10

  val cardHeigth: Int = sizeTenth * 2
  val playerWidth: Int = sizeTenth * 6

  val horizontalPlayerDims = (cardHeigth, playerWidth)
  val verticalPlayerDims = (playerWidth, cardHeigth)

  val centerTableCoords =
    (widthCenter - cardHeigth / 2, heightCenter - cardHeigth / 2)
  val centerTableDims = (cardHeigth, cardHeigth)

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

  val downPlayerConfig = (downPlayerCoords, verticalPlayerDims, Vertical)
  val upPlayerConfig = (upPlayerCoords, verticalPlayerDims, Vertical)
  val leftPlayerConfig = (leftPlayerCoords, horizontalPlayerDims, Horizontal)
  val rightPlayerConfig = (rightPlayerCoords, horizontalPlayerDims, Horizontal)

  val playerPositionConfigurations
      : Map[Int, List[((Int, Int), (Int, Int), GridLayoutOrientation)]] =
    Map.empty
      + (2 -> List(downPlayerConfig, upPlayerConfig))
      + (3 -> List(leftPlayerConfig, downPlayerConfig, rightPlayerConfig))
      + (4 -> List(
        upPlayerConfig,
        leftPlayerConfig,
        downPlayerConfig,
        rightPlayerConfig
      ))
