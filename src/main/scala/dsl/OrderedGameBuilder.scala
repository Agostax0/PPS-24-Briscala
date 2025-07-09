package dsl

import dsl.BuilderStep.*
import dsl.types.HandRule.HandRule
import dsl.types.PlayRule.PlayRule
import dsl.types.PointsRule.PointsRule
import dsl.types.WinRule.WinRule
import engine.model.{BotType, FullEngineModel}

enum BuilderStep:
  case Initial
  case Named
  case WithPlayerCount
  case WithPlayers
  case WithSuits
  case WithRanks
  case WithHandSizeSet
  case Ready
object BuilderStep:
  def getNextStep(from: BuilderStep): BuilderStep = from match
    case Initial         => Named
    case Named           => WithPlayerCount
    case WithPlayerCount => WithPlayers
    case WithPlayers     => WithSuits
    case WithSuits       => WithRanks
    case WithRanks       => WithHandSizeSet
    case WithHandSizeSet => Ready
    case Ready           => Ready

trait OrderedGameBuilder extends GameBuilder

object OrderedGameBuilder:
  def apply(name: String, builder: GameBuilder): GameBuilder =
    OrderedGameBuilderImpl(name, builder)

  private class OrderedGameBuilderImpl(
      override val gameName: String,
      private val builder: GameBuilder = GameBuilder(""),
      var currentStep: BuilderStep = Initial
  ) extends OrderedGameBuilder:
    currentStep = if gameName.isEmpty then Initial else Named

    private def validateStep(
        nextStep: BuilderStep,
        calledMethod: String
    ): Unit =
      if !isStepValid(nextStep) then
        throw new IllegalStateException(
          s"$calledMethod called in wrong order. At: $currentStep is required: ${getNextStep(currentStep)}"
        )

    private def isStepValid(requiredStep: BuilderStep): Boolean =
      currentStep match
        case WithPlayers if requiredStep == WithPlayers => true
        case _ => getNextStep(currentStep) == requiredStep

    override def simpleEquals(obj: Any): Boolean = builder.simpleEquals(obj)

    override def addPlayer(name: String): GameBuilder =
      validateStep(WithPlayers, "addPlayer")
      currentStep = WithPlayers
      builder.addPlayer(name)

    override def addBotPlayer(name: String, botType: BotType): GameBuilder =
      validateStep(WithPlayers, "addBotPlayer")
      currentStep = WithPlayers
      builder.addBotPlayer(name, botType)

    override def setPlayers(n: Int): GameBuilder =
      validateStep(WithPlayerCount, "setPlayers")
      currentStep = WithPlayerCount
      builder.setPlayers(n)

    override def setSuits(suits: List[String]): GameBuilder =
      validateStep(WithSuits, "setSuits")
      currentStep = WithSuits
      builder.setSuits(suits)

    override def setRanks(ranks: List[String]): GameBuilder =
      validateStep(WithRanks, "setRanks")
      currentStep = WithRanks
      builder.setRanks(ranks)

    override def setPlayersHands(handSize: Int): GameBuilder =
      validateStep(WithHandSizeSet, "setPlayerHands")
      currentStep = WithHandSizeSet
      builder.setPlayersHands(handSize)

    override def setStartingPlayer(name: String): GameBuilder =
      validateStep(Ready, "setStartingPlayer")
      currentStep = Ready
      builder.setStartingPlayer(name)

    override def setPointRule(rule: PointsRule): GameBuilder =
      validateStep(Ready, "setPointRule")
      currentStep = Ready
      builder.setPointRule(rule)

    override def setPlayRule(rule: PlayRule): GameBuilder =
      validateStep(Ready, "setPlayRule")
      currentStep = Ready
      builder.setPlayRule(rule)

    override def setHandRule(rule: HandRule): GameBuilder =
      validateStep(Ready, "setHandRule")
      currentStep = Ready
      builder.setHandRule(rule)

    override def setWinRule(rule: WinRule): GameBuilder =
      validateStep(Ready, "setWinRule")
      currentStep = Ready
      builder.setWinRule(rule)

    override def addTeam(names: List[String]): GameBuilder =
      validateStep(Ready, "addTeam")
      currentStep = Ready
      builder.addTeam(names)

    override def setBriscolaSuit(suit: String): GameBuilder =
      validateStep(Ready, "setBriscolaSuit")
      currentStep = Ready
      builder.setBriscolaSuit(suit)

    override def build(): FullEngineModel =
      if currentStep == Ready | currentStep == WithHandSizeSet then
        builder.build()
      else
        throw new IllegalStateException(
          s"Expected a completed builder but got a $currentStep one"
        )
