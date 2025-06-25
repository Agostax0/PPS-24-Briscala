package dsl.syntax

import dsl.GameBuilder
import dsl.syntax.SyntacticSugar.{PlayerSyntacticSugar, ToSyntacticSugar}

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

  object HandBuilder:
    def apply(gameBuilder: GameBuilder, handSize: Int): HandBuilder =
      new HandBuilderImpl(gameBuilder, handSize)

  trait HandBuilder:
    infix def cards(to: ToSyntacticSugar): HandSyntaxBuilder

  private class HandBuilderImpl(gameBuilder: GameBuilder, handSize: Int) extends HandBuilder:
    infix def cards(to: ToSyntacticSugar): HandSyntaxBuilder =
      HandSyntaxBuilder(gameBuilder, handSize)

  object HandSyntaxBuilder:
    def apply(gameBuilder: GameBuilder, handSize: Int): HandSyntaxBuilder =
      new HandSyntaxBuilderImpl(gameBuilder, handSize)
      
  trait HandSyntaxBuilder:
    infix def every(player: PlayerSyntacticSugar): GameBuilder
    
  private class HandSyntaxBuilderImpl(gameBuilder: GameBuilder, handSize: Int) extends HandSyntaxBuilder:
    infix def every(player: PlayerSyntacticSugar): GameBuilder =
      gameBuilder.setPlayersHands(handSize)
