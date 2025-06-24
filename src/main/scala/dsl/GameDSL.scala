package dsl

import dsl.syntax.SyntacticSugar.PlayerSyntacticSugar
import dsl.syntax.SyntacticSugarBuilder.{PlayerBuilder, PlayerCountBuilder}

object GameDSL:
  private var builder: GameBuilder = GameBuilder("")

  def apply(gameBuilder: GameBuilder): Unit = builder = gameBuilder
  implicit def game: GameBuilder = builder

  extension (gameBuilder: GameBuilder)

    /** Creates a new game with the given name. To be used as first rule: it
      * erases any previous configuration.
      * @param name
      *   Game name
      * @return
      *   The builder for the game
      */
    infix def is(name: String): GameBuilder =
      builder = GameBuilder(name)
      builder

    infix def has(playerCount: Int): PlayerCountBuilder =
      PlayerCountBuilder(gameBuilder, playerCount)

    infix def has(playerSyntax: PlayerSyntacticSugar): PlayerBuilder =
      PlayerBuilder(gameBuilder)
