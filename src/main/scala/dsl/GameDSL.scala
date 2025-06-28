package dsl

import dsl.syntax.SyntacticSugar.{PlayerSyntacticSugar, PointsSyntacticSugar, RulesSyntacticSugar, StartsSyntacticSugar}
import dsl.syntax.SyntacticSugarBuilder.*
import dsl.syntax.SyntacticSugar.{
  PlayerSyntacticSugar,
  PointsSyntacticSugar,
  StartsSyntacticSugar,
  TeamSyntacticSugar
}
import dsl.syntax.SyntacticSugarBuilder.{
  HandBuilder,
  PlayerBuilder,
  PlayerCountBuilder,
  PointsBuilder,
  StartingTurnBuilder,
  TeamBuilder
}

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
     *
      * @param playerSyntax
      *   "PlayerSyntacticSugar" instance representing the player
      * @return
      *   The builder for the game
      */
    infix def has(playerSyntax: PlayerSyntacticSugar): PlayerBuilder =
      PlayerBuilder(gameBuilder)

    infix def has(teamSyntax: TeamSyntacticSugar): TeamBuilder =
      TeamBuilder(gameBuilder)

    /** Adds 4 suits with distinct names.
      *
      * To be used like this:
      * {{{
      *   game suitsAre ("Cups", "Coins", "Swords", "Batons")
      * }}}
      *
      * @param suits
      *   The name of the suits
      * @return
      *   The builder for the game
      */
    infix def suitsAre(suits: String*): GameBuilder =
      gameBuilder.addSuits(suits.toList)

    /** Adds the cards to build the deck, creating a card for every suit.
     *
     * To be used like this:
     * {{{
     *   game ranksAre ("2", "4", "5", "6", "7", "Knave", "Knight", "King", "3", "Ace")
     * }}}
     *
     * @param ranks
     * The name of the suits
     * @return
     * The builder for the game
     */
    infix def ranksAre(ranks: String*): GameBuilder =
      gameBuilder.addRanks(ranks.toList)


    /** Sets the number of cards to be given to each player at the start of each turns
     *
     * To be used like this:
     *  {{{
     *   game gives 10 cards to every player
     *  }}}
     *
     * @param handSize
     *  The number of cards to be given to each player
     * @return
     *  The builder for the game
     */
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

    /** Sets the points for each card, based on the card name and/or suit.
     *
     * To be used like this:
     * {{{
     *   game card points are:
     *     (name, suit) =>
     *       name match
     *         case "Ace" => 10
     *         case _ => 0
     * }}}
 *
     * @param points
     *  syntactic sugar
     * @return
     *  The builder for the game
     */
    infix def card(points: PointsSyntacticSugar): PointsBuilder =
      PointsBuilder(gameBuilder)

    /** Sets the briscola to be used in game.
     *
     * To be used like this:
     * {{{
     *   game briscolaIs "Cups"
     * }}}
     *
     * @param suit
     *  the suit of the briscola
     * @return
     * The builder for the game
     */
    infix def briscolaIs(suit: String): GameBuilder =
      gameBuilder.addBriscolaSuit(suit)

    /** Sets the rules for the hand of cards (which cards can be played and which not)
     *
     * To be used like this:
     * {{{
     *   game hand rules are:
     *     (cardsOnTable, playerHand, playedCard) =>
     *       given List[CardModel] = cardsOnTable
     *       given DeckModel = playerHand
     *       given CardModel = playedCard
     *
     *       freeStart or followFirstSuit
     * }}}
     *
     * @param rules
     *  syntactic sugar for hand rules
     * @return
     *  The builder for the game
     */
    infix def hand(rules: RulesSyntacticSugar): HandRuleBuilder =
      HandRuleBuilder(gameBuilder)

    /** Sets the rules for the cards (which player wins the turn)
     *
     * To be used like this:
     * {{{
     *
     * }}}
     *
     * @param rules
     *  syntactic sugar for play rules
     * @return
     *  The builder for the game
     */
    infix def play(rules: RulesSyntacticSugar): PlayRulesBuilder =
      PlayRulesBuilder(gameBuilder)
