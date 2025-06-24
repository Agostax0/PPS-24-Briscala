package engine.view

object ElementsPositionManager:

  val windowWidth = 1000
  val windowHeight = 1000

  val horizontalPlayerDims = (windowWidth / 2, windowHeight / 10)
  val verticalPlayerDims = (windowWidth / 10, windowHeight / 2)

  val widthPadding = windowWidth / 12
  val heightPadding = windowHeight / 12

  val centerPlayerCoords = (windowWidth / 5, windowHeight / 5)
  val upPlayerCoords = (widthPadding, heightPadding)
  val rightPlayerCoords =
    (windowWidth - (verticalPlayerDims._1 + widthPadding), heightPadding)
  val downPlayerCoords = (
    windowWidth - (horizontalPlayerDims._1 + widthPadding),
    windowHeight - (horizontalPlayerDims._2 + heightPadding)
  )
  val leftPlayerCoords =
    (widthPadding, windowHeight - (verticalPlayerDims._2 + heightPadding))
