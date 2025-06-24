package dsl.syntax

trait SyntacticSugar

object SyntacticSugar:
  implicit val player: PlayerSyntacticSugar = PlayerSyntacticSugar

  trait PlayerSyntacticSugar extends SyntacticSugar
  private object PlayerSyntacticSugar extends PlayerSyntacticSugar