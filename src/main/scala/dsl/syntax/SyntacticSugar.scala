package dsl.syntax

trait SyntacticSugar

object SyntacticSugar:
  implicit val player: PlayerSyntacticSugar = PlayerSyntacticSugar
  implicit val to: ToSyntacticSugar = ToSyntacticSugar
  implicit val from: FromSyntacticSugar = FromSyntacticSugar
  implicit val starts: StartsSyntacticSugar = StartsSyntacticSugar
  implicit val points: PointsSyntacticSugar = PointsSyntacticSugar

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
