package dsl.syntax

import dsl.GameBuilder
import dsl.syntax.SyntacticSugar.{PlayerSyntacticSugar, ToSyntacticSugar}
import dsl.types.{HandRule, PlayRule, PointsRule}
import engine.model.{CardModel, DeckModel, PlayerModel}

object SyntacticSugarBuilder:

  trait PlayerCountBuilder:
    infix def players: GameBuilder
  object PlayerCountBuilder:
    def apply(gameBuilder: GameBuilder, playerCount: Int): PlayerCountBuilder =
      new PlayerCountBuilderImpl(gameBuilder, playerCount)
    private class PlayerCountBuilderImpl(
        gameBuilder: GameBuilder,
        playerCount: Int
    ) extends PlayerCountBuilder:
      infix def players: GameBuilder =
        gameBuilder.setPlayers(playerCount)

  trait PlayerBuilder:
    infix def called(name: String): GameBuilder
  object PlayerBuilder:
    def apply(gameBuilder: GameBuilder): PlayerBuilder =
      new PlayerBuilderImpl(gameBuilder)
    private class PlayerBuilderImpl(gameBuilder: GameBuilder)
        extends PlayerBuilder:
      infix def called(name: String): GameBuilder =
        gameBuilder.addPlayer(name)

  trait HandBuilder:
    infix def cards(to: ToSyntacticSugar): ToBuilder
  object HandBuilder:
    def apply(gameBuilder: GameBuilder, handSize: Int): HandBuilder =
      new HandBuilderImpl(gameBuilder, handSize)
    private class HandBuilderImpl(gameBuilder: GameBuilder, handSize: Int)
        extends HandBuilder:
      infix def cards(to: ToSyntacticSugar): ToBuilder =
        ToBuilder(gameBuilder, handSize)

  trait ToBuilder:
    infix def every(player: PlayerSyntacticSugar): GameBuilder
  private object ToBuilder:
    def apply(gameBuilder: GameBuilder, handSize: Int): ToBuilder =
      new ToBuilderImpl(gameBuilder, handSize)
    private class ToBuilderImpl(gameBuilder: GameBuilder, handSize: Int)
        extends ToBuilder:
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

  trait HandRuleBuilder:
    infix def are(
        handRules: (List[CardModel], DeckModel, CardModel) => Boolean
    ): GameBuilder
  object HandRuleBuilder:
    def apply(gameBuilder: GameBuilder): HandRuleBuilder =
      new HandRulesBuilderImpl(
        gameBuilder
      )
    private class HandRulesBuilderImpl(builder: GameBuilder)
        extends HandRuleBuilder:
      override infix def are(
          handRules: (List[CardModel], DeckModel, CardModel) => Boolean
      ): GameBuilder =
        builder.addHandRule(HandRule(handRules))
        builder

  trait PlayRulesBuilder:
    infix def are(
        playRules: (List[(PlayerModel, CardModel)] => Option[PlayerModel])*
    ): GameBuilder
  object PlayRulesBuilder:
    def apply(gameBuilder: GameBuilder): PlayRulesBuilder =
      new PlayRulesBuilderImpl(
        gameBuilder
      )
    private class PlayRulesBuilderImpl(builder: GameBuilder)
        extends PlayRulesBuilder:
      override infix def are(
          playRules: (List[(PlayerModel, CardModel)] => Option[PlayerModel])*
      ): GameBuilder =
        playRules.foreach(rule => builder.addPlayRule(PlayRule(rule)))
        builder
