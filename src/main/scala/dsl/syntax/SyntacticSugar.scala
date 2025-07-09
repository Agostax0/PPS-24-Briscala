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

  trait EntitySyntacticSugar extends SyntacticSugar

  trait RandomBotSyntacticSugar extends EntitySyntacticSugar
  private object RandomBotSyntacticSugar extends RandomBotSyntacticSugar

  trait SmartBotSyntacticSugar extends EntitySyntacticSugar
  private object SmartBotSyntacticSugar extends SmartBotSyntacticSugar

  trait PlayerSyntacticSugar extends EntitySyntacticSugar
  private object PlayerSyntacticSugar extends PlayerSyntacticSugar

  trait ToSyntacticSugar extends SyntacticSugar
  private object ToSyntacticSugar extends ToSyntacticSugar

  trait FromSyntacticSugar extends SyntacticSugar
  private object FromSyntacticSugar extends FromSyntacticSugar

  trait StartsSyntacticSugar extends SyntacticSugar
  private object StartsSyntacticSugar extends StartsSyntacticSugar

  trait PointsSyntacticSugar extends SyntacticSugar
  private object PointsSyntacticSugar extends PointsSyntacticSugar

  trait TeamSyntacticSugar extends SyntacticSugar
  private object TeamSyntacticSugar extends TeamSyntacticSugar

  trait RulesSyntacticSugar extends SyntacticSugar
  private object RulesSyntacticSugar extends RulesSyntacticSugar

  trait TakesSyntacticSugar extends SyntacticSugar
  private object TakesSyntacticSugar extends TakesSyntacticSugar

  trait CardFilterSyntacticSugar extends SyntacticSugar
  private object CardFilterSyntacticSugar extends CardFilterSyntacticSugar

  trait SuitSyntacticSugar extends CardFilterSyntacticSugar
  private object SuitSyntacticSugar extends SuitSyntacticSugar

  trait RankSyntacticSugar extends CardFilterSyntacticSugar
  private object RankSyntacticSugar extends RankSyntacticSugar

  sealed trait CardPositionSyntacticSugar extends SyntacticSugar
  private object CardPositionSyntacticSugar extends CardPositionSyntacticSugar

  trait FirstCardSyntacticSugar extends CardPositionSyntacticSugar
  private object FirstCardSyntacticSugar extends FirstCardSyntacticSugar

  trait LastCardSyntacticSugar extends CardPositionSyntacticSugar
  private object LastCardSyntacticSugar extends LastCardSyntacticSugar
