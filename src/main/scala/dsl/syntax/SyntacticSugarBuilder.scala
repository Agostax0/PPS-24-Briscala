package dsl.syntax

import dsl.GameBuilder
import dsl.syntax.SyntacticSugar.{PlayerSyntacticSugar, ToSyntacticSugar}
import dsl.types.PointsRule

object SyntacticSugarBuilder:

  object PlayerCountBuilder:
    def apply(gameBuilder: GameBuilder, playerCount: Int): PlayerCountBuilder =
      new PlayerCountBuilderImpl(gameBuilder, playerCount)

  trait PlayerCountBuilder:
    infix def players: GameBuilder

  private class PlayerCountBuilderImpl(
      gameBuilder: GameBuilder,
      playerCount: Int
  ) extends PlayerCountBuilder:
    infix def players: GameBuilder =
      gameBuilder.setPlayers(playerCount)

  object PlayerBuilder:
    def apply(gameBuilder: GameBuilder): PlayerBuilder =
      new PlayerBuilderImpl(gameBuilder)

  trait PlayerBuilder:
    infix def called(name: String): GameBuilder

  private class PlayerBuilderImpl(gameBuilder: GameBuilder)
      extends PlayerBuilder:
    infix def called(name: String): GameBuilder =
      gameBuilder.addPlayer(name)

  object HandBuilder:
    def apply(gameBuilder: GameBuilder, handSize: Int): HandBuilder =
      new HandBuilderImpl(gameBuilder, handSize)

  trait HandBuilder:
    infix def cards(to: ToSyntacticSugar): HandSyntaxBuilder

  private class HandBuilderImpl(gameBuilder: GameBuilder, handSize: Int)
      extends HandBuilder:
    infix def cards(to: ToSyntacticSugar): HandSyntaxBuilder =
      HandSyntaxBuilder(gameBuilder, handSize)

  object HandSyntaxBuilder:
    def apply(gameBuilder: GameBuilder, handSize: Int): HandSyntaxBuilder =
      new HandSyntaxBuilderImpl(gameBuilder, handSize)

  trait HandSyntaxBuilder:
    infix def every(player: PlayerSyntacticSugar): GameBuilder

  private class HandSyntaxBuilderImpl(gameBuilder: GameBuilder, handSize: Int)
      extends HandSyntaxBuilder:
    infix def every(player: PlayerSyntacticSugar): GameBuilder =
      gameBuilder.setPlayersHands(handSize)

  trait StartingTurnBuilder:
    infix def from(name: String): GameBuilder

  object StartingTurnBuilder:
    def apply(gameBuilder: GameBuilder): StartingTurnBuilder =
      new StartingTurnBuilderImpl(gameBuilder)

    private class StartingTurnBuilderImpl(builder: GameBuilder)
        extends StartingTurnBuilder:
      override infix def from(name: String): GameBuilder =
        builder.setStartingPlayer(name)

  trait PointsBuilder:
    infix def are(pointRules: ((String, String) => Int)*): GameBuilder

  object PointsBuilder:
    def apply(gameBuilder: GameBuilder): PointsBuilder = new PointsBuilderImpl(
      gameBuilder
    )

    private class PointsBuilderImpl(builder: GameBuilder) extends PointsBuilder:
      override infix def are(
          pointRules: ((String, String) => Int)*
      ): GameBuilder =
        pointRules.foreach(rule => builder.addPointRule(PointsRule(rule)))
        builder

  trait TeamBuilder:
    infix def composedOf(name: String*): GameBuilder

  object TeamBuilder:
    def apply(gameBuilder: GameBuilder): TeamBuilder = new TeamBuilderImpl(
      gameBuilder
    )

    private class TeamBuilderImpl(builder: GameBuilder) extends TeamBuilder:
      override infix def composedOf(names: String*): GameBuilder =
        builder.addTeam(names.toList)
        builder
