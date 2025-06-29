package dsl.syntax

trait SyntacticSugar

object SyntacticSugar:
  implicit val player: PlayerSyntacticSugar = PlayerSyntacticSugar
  implicit val to: ToSyntacticSugar = ToSyntacticSugar
  implicit val from: FromSyntacticSugar = FromSyntacticSugar
  implicit val starts: StartsSyntacticSugar = StartsSyntacticSugar
  implicit val points: PointsSyntacticSugar = PointsSyntacticSugar
  implicit val team: TeamSyntacticSugar = TeamSyntacticSugar
  implicit val rules: RulesSyntacticSugar = RulesSyntacticSugar
  implicit val takes: TakesSyntacticSugar = TakesSyntacticSugar
  implicit val suit: SuitSyntacticSugar = SuitSyntacticSugar
  implicit val rank: RankSyntacticSugar = RankSyntacticSugar
  implicit val first: CardPositionSyntacticSugar = FirstCardSyntacticSugar
  implicit val last: CardPositionSyntacticSugar = LastCardSyntacticSugar

  trait PlayerSyntacticSugar extends SyntacticSugar
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

  trait SuitSyntacticSugar extends SyntacticSugar
  private object SuitSyntacticSugar extends SuitSyntacticSugar

  trait RankSyntacticSugar extends SyntacticSugar
  private object RankSyntacticSugar extends RankSyntacticSugar

  sealed trait CardPositionSyntacticSugar extends SyntacticSugar
  private object CardPositionSyntacticSugar extends CardPositionSyntacticSugar

  trait FirstCardSyntacticSugar extends CardPositionSyntacticSugar
  private object FirstCardSyntacticSugar extends FirstCardSyntacticSugar

  trait LastCardSyntacticSugar extends CardPositionSyntacticSugar
  private object LastCardSyntacticSugar extends LastCardSyntacticSugar
