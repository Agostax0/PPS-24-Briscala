package engine.controller

import engine.controller.EventParser.parseEvent
import engine.model.{BotPlayerModel, CardModel, FullEngineModel, PlayerModel}
import engine.view.EngineView
import engine.view.WindowStateImpl.{Window, initialWindow}
import engine.view.monads.States.State
import engine.view.ElementsPositionManager.*
import engine.view.monads.Monads.Monad.seqN

sealed trait GameEvent
case class CardPlayedEvent(playerName: String, card: CardModel) extends GameEvent
case object InvalidEvent extends GameEvent

object EventParser:
  def parseEvent(event: String): Either[String, GameEvent] =
    event.split("::").toList match
      case playerName :: cardInfo :: Nil =>
        parseCard(cardInfo).map(card => CardPlayedEvent(playerName, card))
      case _ =>
        Left(s"Invalid event format: expected 'playerName::cardInfo', got '$event'")

  private def parseCard(cardInfo: String): Either[String, CardModel] =
    cardInfo.split(" ").toList match
      case name :: rankStr :: suit :: Nil =>
        rankStr.toIntOption match
          case Some(rank) => Right(CardModel(name, rank, suit))
          case None => Left(s"Invalid rank: $rankStr")
      case _ =>
        Left(s"Invalid card format: expected 'name rank suit', got '$cardInfo'")

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

    private var totalRounds: Int = 0
    private var playerTurn: Int = 0

    override def start(): Unit =
      val initialState =
        for
          _ <- view.addTurnHistory()
          _ <- view.addTable()
          _ <- model.players.foldLeft(unitState(): State[Window, Unit]):
            (state, player) =>
              for
                _ <- view.addPlayer(player.name, model.players.size)
                _ <- state
                _ <- renderHand(player)
              yield ()
          _ <- checkBot()
        yield ()


      val windowCreation = initialState.flatMap(_ => view.show)

      val windowEventsHandler: State[Window, Unit] = for
        events <- windowCreation
        _ <- seqN(events.map(event =>
          val parsedEvent = parseEvent(event)
          parsedEvent match
            case Left(error) =>
              println(s"Error parsing event: $error")
              unitState()
            case Right(CardPlayedEvent(playerName, card)) =>
              handleCardPlayed(playerName, card.name, card.rank.toString, card.suit)
            case _ =>
              println("Invalid event received")
              unitState()
        ))
      yield ()

      windowEventsHandler.run(initialWindow)

    private def playCardProgrammatically(bot: BotPlayerModel): State[Window, Unit] =
      val card: CardModel = model.botPlayCard(bot)
      for
        _ <- handleCardPlayed(bot.name, card.name, card.rank.toString, card.suit)
      yield()

    private def checkBot():State[Window, Unit] = {
      model.activePlayer match
        case bot: BotPlayerModel if bot.hand.view.nonEmpty => playCardProgrammatically(bot)
        case _ => unitState()
    }


    private def handleCardPlayed(playerName: String, name: String , rank: String, suit: String): State[Window, Unit] =
      val card = CardModel(name, rank.toInt, suit)
      model.players.find(playerName == _.name) match
        case Some(player) => playCard(player, card)
        case None =>
          println(s"Player $playerName not found.")
          unitState()

    private def resetTurn(): Unit =
      playerTurn = 0
      totalRounds += 1

    private def drawCards(): State[Window, Unit] =
      model.giveCardsToPlayers(1)
      for
        _ <- model.players.foldLeft( unitState(): State[Window, Unit]):
          (state, player) =>
            for
              _ <- state
              _ <- view.removeCardsFromPlayer(player.name)
              _ <- renderHand(player)
            yield()
      yield()

    private def endTurn(): State[Window, Unit] =
      if playerTurn == model.players.size then
        model.computeTurn()
        resetTurn()
        println(s"End of turn, ${model.activePlayer.name} is the winner of this turn.")
        for
          _ <- view.clearTable()
          _ <- view.addTurnWinner(model.activePlayer.name, totalRounds.toString)
          _ <- drawCards()
          _ <- endGame()
          _ <- checkBot()
        yield()
      else {
        checkBot()
      }

    private def endGame(): State[Window, Unit] =
      if model.players.forall(_.hand.isEmpty) then
        val winningPlayers = model.winningGamePlayers().reduce((a:String, b:String)=>a + " " + b)
        println("THE WINNER IS: " + winningPlayers)
        model.players.foreach(player =>
          println(player.name + ": " + player.score + " points"))
        for
          _ <- view.declareWinner(winningPlayers)
        yield()
      else
        unitState()

    private def playCard(player: PlayerModel, card: CardModel): State[Window, Unit] =
      if model.playCard(player, card) then
        println(s"${player.name} played ${card.name} of ${card.suit}")
        playerTurn += 1
        for
          _ <- view.removeCardFromPlayer(player.name, card)
          _ <- view.addCardToTable(player.name, card)
          _ <- endTurn()
        yield()
      else
        unitState()

    private def unitState(): State[Window, Unit] = State(s => (s, ()))

    private def renderHand(player: PlayerModel): State[Window, Unit] =
      player.hand.view.foldLeft(unitState(): State[Window, Unit]):
        (nestedState, card) =>
        for
          _ <- nestedState
          _ <- view.addCardToPlayer(player.name, card)
        yield ()
