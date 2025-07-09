package dsl

import dsl.BuilderStep.*
import dsl.types.HandRule.HandRule
import dsl.types.PlayRule.PlayRule
import dsl.types.PointsRule.PointsRule
import dsl.types.WinRule.WinRule
import engine.model.{BotType, FullEngineModel}

/** An enumeration of the builder steps to follow inside an OrderedGameBuilder
  */
enum BuilderStep:
  /** The initial state of a builder, without any info
    *
    * To this step follows: {{{Named}}}
    */
  case Initial

  /** A named builder
    *
    * To this step follows: {{{WithPlayerCount}}}
    */
  case Named

  /** A builder with a specified name count
    *
    * To this step follows: {{{WithPlayers}}}
    */
  case WithPlayerCount

  /** A builder which expects to add players
    *
    * To this step follows: {{{WithPlayers}}} or {{{WithSuits}}}
    */
  case WithPlayers

  /** A builder with defined suits
    *
    * To this step follows: {{{WithRanks}}}
    */
  case WithSuits

  /** A builder with defined ranks
    *
    * To this step follows: {{{WithHandSizeSet}}}
    */
  case WithRanks

  /** A builder with a specified player hand size set
    *
    * To this step follows: {{{Ready}}}
    */
  case WithHandSizeSet

  /** A complete builder ready for use
    *
    * To this step follows: {{{Ready}}} for further configuration
    */
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

/** OrderedGameBuilder is a trait that defines the methods to build a game in a
  * specific order
  */
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

    /** Adds a player to a game with a fixed player size.
      *
      * @param name
      *   the name of the player
      * @return
      *   the GameBuilder instance with the added player
      */
    override def addPlayer(name: String): GameBuilder =
      validateStep(WithPlayers, "addPlayer")
      currentStep = WithPlayers
      builder.addPlayer(name)

    /** Adds a bot player to a game with a fixed player size.
      *
      * @param name
      *   the name of the bot player
      * @param botType
      *   the type of the bot
      * @return
      *   the GameBuilder instance with the added bot player
      */
    override def addBotPlayer(name: String, botType: BotType): GameBuilder =
      validateStep(WithPlayers, "addBotPlayer")
      currentStep = WithPlayers
      builder.addBotPlayer(name, botType)

    /** Sets the number of players in the named game.
      *
      * @param n
      *   the number of players
      * @return
      *   the GameBuilder instance with the set number of players
      */
    override def setPlayers(n: Int): GameBuilder =
      validateStep(WithPlayerCount, "setPlayers")
      currentStep = WithPlayerCount
      builder.setPlayers(n)

    /** Sets the suits of the cards in the game with players.
     *
     * @param suits
     * the list of suits
     * @return
     * the GameBuilder instance with the set suits
     */
    override def setSuits(suits: List[String]): GameBuilder =
      validateStep(WithSuits, "setSuits")
      currentStep = WithSuits
      builder.setSuits(suits)

    /** Sets the ranks of the cards in the game with suits.
     *
     * @param ranks
     * the list of ranks
     * @return
     * the GameBuilder instance with the set ranks
     */
    override def setRanks(ranks: List[String]): GameBuilder =
      validateStep(WithRanks, "setRanks")
      currentStep = WithRanks
      builder.setRanks(ranks)

    /** Sets the size of the hands for each player after deciding suits and ranks of cards.
     *
     * @param handSize
     * the size of the hands
     * @return
     * the GameBuilder instance with the set hand size
     */
    override def setPlayersHands(handSize: Int): GameBuilder =
      validateStep(WithHandSizeSet, "setPlayerHands")
      currentStep = WithHandSizeSet
      builder.setPlayersHands(handSize)

    /** Sets the starting player of the complete game.
     *
     * @param name
     * the name of the starting player
     * @return
     * the GameBuilder instance with the set starting player
     */
    override def setStartingPlayer(name: String): GameBuilder =
      validateStep(Ready, "setStartingPlayer")
      currentStep = Ready
      builder.setStartingPlayer(name)

    /** Sets the point rules for the complete game.
     *
     * @param rule
     * the points rule to be set
     * @return
     * the GameBuilder instance with the set point rules
     */
    override def setPointRule(rule: PointsRule): GameBuilder =
      validateStep(Ready, "setPointRule")
      currentStep = Ready
      builder.setPointRule(rule)

    /** Sets the play rules for the complete game.
     *
     * @param rule
     * the play rule to be set
     * @return
     * the GameBuilder instance with the set play rules
     */
    override def setPlayRule(rule: PlayRule): GameBuilder =
      validateStep(Ready, "setPlayRule")
      currentStep = Ready
      builder.setPlayRule(rule)

    /** Sets the hand rule for the complete game.
     *
     * @param rule
     * the hand rule to be set
     * @return
     * the GameBuilder instance with the set hand rule
     */
    override def setHandRule(rule: HandRule): GameBuilder =
      validateStep(Ready, "setHandRule")
      currentStep = Ready
      builder.setHandRule(rule)

    /** Sets the win rule for the complete game.
     *
     * @param rule
     * the win rule to be set
     * @return
     * the GameBuilder instance with the set win rule
     */
    override def setWinRule(rule: WinRule): GameBuilder =
      validateStep(Ready, "setWinRule")
      currentStep = Ready
      builder.setWinRule(rule)

    /** Adds a team to the complete game.
     *
     * @param names
     * the list of player names in the team
     * @return
     * the GameBuilder instance with the added team
     * @throws IllegalArgumentException
     * if any player in the team does not exist or is already in another team
     */
    override def addTeam(names: List[String]): GameBuilder =
      validateStep(Ready, "addTeam")
      currentStep = Ready
      builder.addTeam(names)

    /** Sets the suit of the briscola card for the complete game.
     *
     * @param suit
     * the suit of the briscola card
     * @return
     * the GameBuilder instance with the set briscola suit
     * @throws IllegalArgumentException
     * if the suit is not defined in the game
     */
    override def setBriscolaSuit(suit: String): GameBuilder =
      validateStep(Ready, "setBriscolaSuit")
      currentStep = Ready
      builder.setBriscolaSuit(suit)

    /** Builds the game model.
     *
     * @return
     * the FullEngineModel representing the game
     * @throws IllegalArgumentException
     * if the number of players does not match the set player count or if any
     * other required field is not set correctly
     */
    override def build(): FullEngineModel =
      if currentStep == Ready | currentStep == WithHandSizeSet then
        builder.build()
      else
        throw new IllegalStateException(
          s"Expected a completed builder but got a $currentStep one"
        )
