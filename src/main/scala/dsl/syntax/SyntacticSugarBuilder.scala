package dsl.syntax

import dsl.GameBuilder

object SyntacticSugarBuilder:

  object PlayerCountBuilder:
    def apply(gameBuilder: GameBuilder, playerCount: Int): PlayerCountBuilder = 
      new PlayerCountBuilderImpl(gameBuilder, playerCount)

  trait PlayerCountBuilder:
    infix def players: GameBuilder
      
  private class PlayerCountBuilderImpl(gameBuilder: GameBuilder, playerCount: Int)
    extends PlayerCountBuilder:
    infix def players: GameBuilder =
      gameBuilder.setPlayers(playerCount)
      
  object PlayerBuilder:
    def apply(gameBuilder: GameBuilder): PlayerBuilder = 
      new PlayerBuilderImpl(gameBuilder)
      
  trait PlayerBuilder:
    infix def called(name: String): GameBuilder

  private class PlayerBuilderImpl(gameBuilder: GameBuilder) extends PlayerBuilder:
    infix def called(name: String): GameBuilder =
      gameBuilder.addPlayer(name)