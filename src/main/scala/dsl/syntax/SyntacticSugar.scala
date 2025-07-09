package dsl.syntax

trait SyntacticSugar

object SyntacticSugar:
  /** Syntactic sugar to make the DSL more natural language-like
    *
    * This is used in the syntax: {{{game has player called "Alice"}}} And
    * {{{game gives 3 cards to every player}}}
    */
  implicit val player: PlayerSyntacticSugar = PlayerSyntacticSugar

  /** Syntactic sugar to make the DSL more natural language-like
    *
    * This is used in the syntax: {{{game has randomBot called "Josh"}}}
    */
  implicit val randomBot: EntitySyntacticSugar = RandomBotSyntacticSugar

  /** Syntactic sugar to make the DSL more natural language-like
    *
    * This is used in the syntax: {{{game has smartBot called "Albert"}}}
    */
  implicit val smartBot: EntitySyntacticSugar = SmartBotSyntacticSugar

  /** Syntactic sugar to make the DSL more natural language-like
    *
    * This is used in the syntax: {{{game gives 3 cards to every player}}}
    */
  implicit val to: ToSyntacticSugar = ToSyntacticSugar

  /** Syntactic sugar to make the DSL more natural language-like
    *
    * This is not used
    */
  implicit val from: FromSyntacticSugar = FromSyntacticSugar

  /** Syntactic sugar to make the DSL more natural language-like
    *
    * This is used in the syntax: {{{game firstTurn starts from "Alice"}}}
    */
  implicit val starts: StartsSyntacticSugar = StartsSyntacticSugar

  /** Syntactic sugar to make the DSL more natural language-like
    *
    * This is used in the syntax: {{{game card points are:}}}
    */
  implicit val points: PointsSyntacticSugar = PointsSyntacticSugar

  /** Syntactic sugar to make the DSL more natural language-like
    *
    * This is used in the syntax: {{{game has team composedOf("Alice", "Bob")}}}
    */
  implicit val team: TeamSyntacticSugar = TeamSyntacticSugar

  /** Syntactic sugar to make the DSL more natural language-like
    *
    * This is used in the syntax: {{{game hand rules are:}}} And
    * {{{game play rules are:}}} And {{{game win rules is:}}}
    */
  implicit val rules: RulesSyntacticSugar = RulesSyntacticSugar

  /** Syntactic sugar to make the DSL more natural language-like
    *
    * This is used in the syntax: {{{highest(suit) that takes is briscolaSuit}}}
    * And {{{highest(rank) that takes follows first card suit}}}
    */
  implicit val takes: TakesSyntacticSugar = TakesSyntacticSugar

  /** Syntactic sugar to make the DSL more natural language-like
    *
    * This is used in the syntax: {{{highest(suit) that takes is briscolaSuit}}}
    */
  implicit val suit: CardFilterSyntacticSugar = SuitSyntacticSugar

  /** Syntactic sugar to make the DSL more natural language-like
    *
    * This is used in the syntax:
    * {{{highest(rank) that takes follows first card suit}}}
    */
  implicit val rank: CardFilterSyntacticSugar = RankSyntacticSugar

  /** Syntactic sugar to make the DSL more natural language-like
    *
    * This is used in the syntax:
    * {{{highest(rank) that takes follows first card suit}}}
    */
  implicit val first: CardPositionSyntacticSugar = FirstCardSyntacticSugar

  /** Syntactic sugar to make the DSL more natural language-like
    *
    * This is used in the syntax:
    * {{{highest(rank) that takes follows last card suit}}}
    */
  implicit val last: CardPositionSyntacticSugar = LastCardSyntacticSugar

  /** Trait to create either a player syntax sugar or a bot syntax sugar
    */
  trait EntitySyntacticSugar extends SyntacticSugar

  /** Trait to create a random bot syntax sugar
    */
  trait RandomBotSyntacticSugar extends EntitySyntacticSugar
  private object RandomBotSyntacticSugar extends RandomBotSyntacticSugar

  /** Trait to create a smart bot syntax sugar
    */
  trait SmartBotSyntacticSugar extends EntitySyntacticSugar
  private object SmartBotSyntacticSugar extends SmartBotSyntacticSugar

  /** Trait to create a player syntax sugar
    */
  trait PlayerSyntacticSugar extends EntitySyntacticSugar
  private object PlayerSyntacticSugar extends PlayerSyntacticSugar

  /** Trait to create a "to" syntax sugar
    */
  trait ToSyntacticSugar extends SyntacticSugar
  private object ToSyntacticSugar extends ToSyntacticSugar

  /** Trait to create a "from" syntax sugar
    */
  trait FromSyntacticSugar extends SyntacticSugar
  private object FromSyntacticSugar extends FromSyntacticSugar

  /** Trait to create a "starts" syntax sugar
    */
  trait StartsSyntacticSugar extends SyntacticSugar
  private object StartsSyntacticSugar extends StartsSyntacticSugar

  /** Trait to create a "points" syntax sugar
    */
  trait PointsSyntacticSugar extends SyntacticSugar
  private object PointsSyntacticSugar extends PointsSyntacticSugar

  /** Trait to create a "team" syntax sugar
    */
  trait TeamSyntacticSugar extends SyntacticSugar
  private object TeamSyntacticSugar extends TeamSyntacticSugar

  /** Trait to create a "rules" syntax sugar
    */
  trait RulesSyntacticSugar extends SyntacticSugar
  private object RulesSyntacticSugar extends RulesSyntacticSugar

  /** Trait to create a "takes" syntax sugar
    */
  trait TakesSyntacticSugar extends SyntacticSugar
  private object TakesSyntacticSugar extends TakesSyntacticSugar

  /** Trait to create a card filter syntax sugar
    */
  trait CardFilterSyntacticSugar extends SyntacticSugar
  private object CardFilterSyntacticSugar extends CardFilterSyntacticSugar

  /** Trait to create a "suit" syntax sugar
    */
  trait SuitSyntacticSugar extends CardFilterSyntacticSugar
  private object SuitSyntacticSugar extends SuitSyntacticSugar

  /** Trait to create a "rank" syntax sugar
    */
  trait RankSyntacticSugar extends CardFilterSyntacticSugar
  private object RankSyntacticSugar extends RankSyntacticSugar

  /** Trait to create a card position syntax sugar
    */
  sealed trait CardPositionSyntacticSugar extends SyntacticSugar
  private object CardPositionSyntacticSugar extends CardPositionSyntacticSugar

  /** Trait to create a "first" syntax sugar
    */
  trait FirstCardSyntacticSugar extends CardPositionSyntacticSugar
  private object FirstCardSyntacticSugar extends FirstCardSyntacticSugar

  /** Trait to create a "last" syntax sugar
    */
  trait LastCardSyntacticSugar extends CardPositionSyntacticSugar
  private object LastCardSyntacticSugar extends LastCardSyntacticSugar
