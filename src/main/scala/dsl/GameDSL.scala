package dsl

import dsl.syntax.SyntacticSugar.{PlayerSyntacticSugar, PointsSyntacticSugar, StartsSyntacticSugar}
import dsl.syntax.SyntacticSugarBuilder.{HandBuilder, PlayerBuilder, PlayerCountBuilder, PointsBuilder, StartingTurnBuilder}

object GameDSL:
  private var builder: GameBuilder = _

  def apply(gameBuilder: GameBuilder): Unit = builder = gameBuilder
  implicit def game: GameBuilder = builder

  extension (gameBuilder: GameBuilder)

    /** Creates a new game with the given name. To be used as first rule: it
      * erases any previous configuration.
      *
      * To be used like this:
      * {{{
      *   game is "Game Name"
      * }}}
      * @param name
      *   Game name
      * @return
      *   The builder for the game
      */
    infix def is(name: String): GameBuilder =
      builder = GameBuilder(name)
      builder

    /** Sets the game to have a specific number of players.
      *
      * To be used like this:
      * {{{
      *   game has 2 players
      * }}}
      * @param playerCount
      *   the number of players expected to be added
      * @return
      *   The builder for the game
      */
    infix def has(playerCount: Int): PlayerCountBuilder =
      PlayerCountBuilder(gameBuilder, playerCount)

    /** Adds a player to the game.
      *
      * To be used like this:
      * {{{
      *   game has player called "Alice"
      * }}}
      * @param playerSyntax
      *   "PlayerSyntacticSugar" instance representing the player
      * @return
      *   The builder for the game
      */
    infix def has(playerSyntax: PlayerSyntacticSugar): PlayerBuilder =
      PlayerBuilder(gameBuilder)

    infix def suitsAre(suits: String*): GameBuilder =
      gameBuilder.addSuits(suits.toList)

    infix def ranksAre(ranks: String*): GameBuilder =
      gameBuilder.addRanks(ranks.toList)

    infix def gives(handSize: Int): HandBuilder =
      HandBuilder(gameBuilder, handSize)

    /** Sets which player plays the first card
      *
      * To be used like this:
      * {{{
      *   game firstTurn starts from "Alice"
      * }}}
      *
      * @param starts
      *   "StartsSyntacticSugar" instance
      * @return
      *   The builder for this game
      */
    infix def firstTurn(starts: StartsSyntacticSugar): StartingTurnBuilder =
      StartingTurnBuilder(gameBuilder)

    infix def card(points: PointsSyntacticSugar): PointsBuilder =
      PointsBuilder(gameBuilder)
