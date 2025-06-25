package dsl.syntax

trait SyntacticSugar

object SyntacticSugar:
  implicit val player: PlayerSyntacticSugar = PlayerSyntacticSugar
  implicit val to: ToSyntacticSugar = ToSyntacticSugar

  trait PlayerSyntacticSugar extends SyntacticSugar
  private object PlayerSyntacticSugar extends PlayerSyntacticSugar
  
  trait ToSyntacticSugar extends SyntacticSugar
  private object ToSyntacticSugar extends ToSyntacticSugar