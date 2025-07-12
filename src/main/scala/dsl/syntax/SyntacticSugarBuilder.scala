package dsl.syntax

import dsl.GameBuilder
import dsl.syntax.SyntacticSugar.*
import dsl.types.{HandRule, PlayRule, PointsRule}
import dsl.syntax.SyntacticSugar.{PlayerSyntacticSugar, ToSyntacticSugar}
import dsl.types.Team.Team
import dsl.types.{HandRule, PlayRule, PointsRule, Team, WinRule}
import engine.model.{BotType, CardModel, DeckModel, PlayerModel}

import scala.language.implicitConversions

object SyntacticSugarBuilder:

  /** Builder for creating a GameBuilder with a specific number of players.
    */
  trait PlayerCountBuilder:
    infix def players: GameBuilder
  object PlayerCountBuilder:
    /** Sets the number of players for the GameBuilder.
      * @param gameBuilder
      *   the GameBuilder to configure
      * @param playerCount
      *   the number of players expected to be added
      * @return
      *   a PlayerCountBuilder that can be used to set the number of players
      */
    def apply(gameBuilder: GameBuilder, playerCount: Int): PlayerCountBuilder =
      new PlayerCountBuilderImpl(gameBuilder, playerCount)
    private class PlayerCountBuilderImpl(
        gameBuilder: GameBuilder,
        playerCount: Int
    ) extends PlayerCountBuilder:
      infix def players: GameBuilder =
        gameBuilder.setPlayers(playerCount)

  /** Builder for adding entities (players or bots) to the GameBuilder.
    */
  trait EntityBuilder:
    infix def called(name: String): GameBuilder
  object EntityBuilder:
    /** Adds players or bots to the GameBuilder.
      * @param gameBuilder
      *   the GameBuilder to configure
      * @param entity
      *   the entity to be added (Player, RandomBot, SmartBot)
      * @return
      *   an EntityBuilder that can be used to set the name of the entity
      */
    def apply(
        gameBuilder: GameBuilder,
        entity: EntitySyntacticSugar
    ): EntityBuilder =
      new EntityBuilderImpl(gameBuilder, entity)
    private class EntityBuilderImpl(
        gameBuilder: GameBuilder,
        entity: EntitySyntacticSugar
    ) extends EntityBuilder:
      infix def called(name: String): GameBuilder =
        entity match
          case _: PlayerSyntacticSugar => gameBuilder.addPlayer(name)
          case _: RandomBotSyntacticSugar =>
            gameBuilder.addBotPlayer(name, BotType.Random)
          case _: SmartBotSyntacticSugar =>
            gameBuilder.addBotPlayer(name, BotType.Smart)

  /** Builder for setting the number of cards in each player's hand to the
    * GameBuilder.
    */
  trait HandBuilder:
    infix def cards(to: ToSyntacticSugar): ToBuilder
  object HandBuilder:
    /** Sets the number of cards in each player's hand for the GameBuilder.
      * @param gameBuilder
      *   the GameBuilder to configure
      * @param handSize
      *   the number of cards in each player's hand
      * @return
      *   a HandBuilder that can be used to set the players' hands
      */
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

  /** Builder for setting the starting player of the GameBuilder.
    */
  trait StartingTurnBuilder:
    infix def from(name: String): GameBuilder
  object StartingTurnBuilder:
    /** Sets the index of the starting player in the GameBuilder.
      * @param gameBuilder
      *   the GameBuilder to configure
      * @return
      *   a StartingTurnBuilder that can be used to set the starting player
      */
    def apply(gameBuilder: GameBuilder): StartingTurnBuilder =
      new StartingTurnBuilderImpl(gameBuilder)
    private class StartingTurnBuilderImpl(builder: GameBuilder)
        extends StartingTurnBuilder:
      override infix def from(name: String): GameBuilder =
        builder.setStartingPlayer(name)

  /** Builder for setting the points rules of the GameBuilder.
    */
  trait PointsBuilder:
    infix def are(pointRules: ((String, String) => Int)*): GameBuilder
  object PointsBuilder:
    /** Adds point rules to the GameBuilder.
      * @param gameBuilder
      *   the GameBuilder to configure
      * @return
      *   a PointsBuilder that can be used to set the point rules
      */
    def apply(gameBuilder: GameBuilder): PointsBuilder = new PointsBuilderImpl(
      gameBuilder
    )
    private class PointsBuilderImpl(builder: GameBuilder) extends PointsBuilder:
      override infix def are(
          pointRules: ((String, String) => Int)*
      ): GameBuilder =
        pointRules.foreach(rule => builder.setPointRule(PointsRule(rule)))
        builder

  /** Builder for creating teams of players in the GameBuilder.
    */
  trait TeamBuilder:
    infix def composedOf(name: String*): GameBuilder
  object TeamBuilder:
    /** Adds teams to the GameBuilder.
      * @param gameBuilder
      *   the GameBuilder to configure
      * @return
      *   a TeamBuilder that can be used to set the team names
      */
    def apply(gameBuilder: GameBuilder): TeamBuilder = new TeamBuilderImpl(
      gameBuilder
    )
    private class TeamBuilderImpl(builder: GameBuilder) extends TeamBuilder:
      override infix def composedOf(names: String*): GameBuilder =
        builder.addTeam(names.toList)
        builder

  /** Builder for setting hand rules in the GameBuilder.
    */
  trait HandRuleBuilder:
    infix def are(
        handRules: (List[CardModel], DeckModel, CardModel) => Boolean
    ): GameBuilder
  object HandRuleBuilder:
    /** Sets the hand rules for the GameBuilder.
      * @param gameBuilder
      *   the GameBuilder to configure
      * @return
      *   a HandRuleBuilder that can be used to set the hand rules
      */
    def apply(gameBuilder: GameBuilder): HandRuleBuilder =
      new HandRulesBuilderImpl(
        gameBuilder
      )
    private class HandRulesBuilderImpl(builder: GameBuilder)
        extends HandRuleBuilder:
      override infix def are(
          handRules: (List[CardModel], DeckModel, CardModel) => Boolean
      ): GameBuilder =
        builder.setHandRule(HandRule(handRules))
        builder

  /** Builder for setting play rules in the GameBuilder.
    */
  trait PlayRulesBuilder:
    infix def are(
        playRules: (List[(PlayerModel, CardModel)] => Option[PlayerModel])*
    ): GameBuilder
  object PlayRulesBuilder:
    /** Sets the play rules for the GameBuilder.
      * @param gameBuilder
      *   the GameBuilder to configure
      * @return
      *   a PlayRulesBuilder that can be used to set the play rules
      */
    def apply(gameBuilder: GameBuilder): PlayRulesBuilder =
      new PlayRulesBuilderImpl(
        gameBuilder
      )
    private class PlayRulesBuilderImpl(builder: GameBuilder)
        extends PlayRulesBuilder:
      override infix def are(
          playRules: (List[(PlayerModel, CardModel)] => Option[PlayerModel])*
      ): GameBuilder =
        playRules.foreach(rule => builder.setPlayRule(PlayRule(rule)))
        builder

  /** Builder for setting win rules in the GameBuilder.
    */
  trait WinRulesBuilder:
    infix def is(
        winRules: (List[Team], List[PlayerModel]) => List[Team]
    ): GameBuilder
  object WinRulesBuilder:
    /** Sets the win rules for the GameBuilder.
      * @param gameBuilder
      *   the GameBuilder to configure
      * @return
      *   a WinRulesBuilder that can be used to set the win rules
      */
    def apply(gameBuilder: GameBuilder): WinRulesBuilder =
      new WinRulesBuilderImpl(
        gameBuilder
      )
    private class WinRulesBuilderImpl(builder: GameBuilder)
        extends WinRulesBuilder:
      override infix def is(
          winRules: (List[Team], List[PlayerModel]) => List[Team]
      ): GameBuilder =
        builder.setWinRule(WinRule(winRules))
        builder

  /** Builder for setting turn-winning cards in the GameBuilder.
    *
    * To be used either like this:
    * {{{highest(suit) that takes is briscolaSuit}}} Or
    * {{{highest(rank) that takes follows first card suit}}}
    */
  trait HighestBuilder:
    infix def that(takes: TakesSyntacticSugar): HighestCardBuilder
  private object HighestBuilder:
    /** Builder for setting turn-winning cards in the GameBuilder.
      *
      * @param filterSyntacticSugar
      *   accepts either "suit" or "rank" syntactic sugar
      * @return
      *   a builder which will choose the highest card of either suit or rank
      */
    def apply(filterSyntacticSugar: CardFilterSyntacticSugar): HighestBuilder =
      new HighestBuilderImpl(filterSyntacticSugar)
    private class HighestBuilderImpl(
        val highestTakingCardProperty: CardFilterSyntacticSugar
    ) extends HighestBuilder:
      override infix def that(takes: TakesSyntacticSugar): HighestCardBuilder =
        HighestCardBuilder.apply(highestTakingCardProperty)

  /** Builder for setting how turn-winning cards are chosen in the GameBuilder.
    *
    * To be used either like this:
    * {{{highest(suit) that takes is briscolaSuit}}} Or
    * {{{highest(rank) that takes follows first card suit}}}
    */
  trait HighestCardBuilder:
    /** Meant for briscola configuration
      *
      * To be used like this: {{{highest(suit) that takes is briscolaSuit}}}
      *
      * @param suit
      *   the highest ranked card has to be of this suit
      * @param cards
      *   the cards at play
      * @return
      *   the player who will win this turn based on the cards at play
      */
    infix def is(suit: String)(using
        cards: List[(PlayerModel, CardModel)]
    ): Option[PlayerModel]

    /** Meant for generic taking configurations
      *
      * To be used like this:
      * {{{highest(rank) that takes follows first card suit}}}
      *
      * @param cardPosition
      *   the card's position to refer to when choosing a winner
      * @return
      *   a builder to choose the reference card property
      */
    infix def follows(
        cardPosition: CardPositionSyntacticSugar
    ): HighestCardInPositionBuilder
  private object HighestCardBuilder:
    def apply(
        highestTakingCardProperty: CardFilterSyntacticSugar
    ): HighestCardBuilder = new HighestCardBuilderImpl(
      highestTakingCardProperty
    )
    private class HighestCardBuilderImpl(
        val filterSyntacticSugar: CardFilterSyntacticSugar
    ) extends HighestCardBuilder:
      override infix def follows(
          cardPosition: CardPositionSyntacticSugar
      ): HighestCardInPositionBuilder =
        HighestCardInPositionBuilder(filterSyntacticSugar, cardPosition)

      override infix def is(briscola: String)(using
          cardsOnTable: List[(PlayerModel, CardModel)]
      ): Option[PlayerModel] =
        val winner = cardsOnTable
          .filter(_._2.suit equals briscola)
          .sortBy(_._2.rank)(using Ordering.Int.reverse)
          .map(_._1)
          .headOption
        winner

  /** Builder for setting the reference card property in choosing the turn
    * winner.
    */
  trait HighestCardInPositionBuilder:
    /** Meant for choosing the reference card property, to choose "taking power"
      * and card dominance.
      *
      * To be used like this:
      * {{{highest(rank) that takes follows first card suit}}}
      * @param property
      *   the property of the card to compare to (expecting "suit" or "rank"
      *   syntax sugar)
      * @param cards
      *   the cards at play
      * @return
      *   the player who will win this turn based on the cards at play
      */
    infix def card(property: CardFilterSyntacticSugar)(using
        cards: List[(PlayerModel, CardModel)]
    ): Option[PlayerModel]

  private object HighestCardInPositionBuilder:
    def apply(
        highestTakingCardProperty: CardFilterSyntacticSugar,
        cardPosition: CardPositionSyntacticSugar
    ): HighestCardInPositionBuilder =
      new HighestCardInPositionBuilderImpl(
        highestTakingCardProperty,
        cardPosition
      )
    private class HighestCardInPositionBuilderImpl(
        val highestTakingCardProperty: CardFilterSyntacticSugar,
        val cardPosition: CardPositionSyntacticSugar
    ) extends HighestCardInPositionBuilder:
      override infix def card(cardPositionProperty: CardFilterSyntacticSugar)(
          using cardsOnTable: List[(PlayerModel, CardModel)]
      ): Option[PlayerModel] =
        val card = getCardByPosition(using cardsOnTable)
        val winner = cardsOnTable
          .filterByProperty(card, cardPositionProperty)
          .sortByHighestProperty
          .map(_._1)
          .headOption
        winner

      private def getCardByPosition(using
          cardsOnTable: List[(PlayerModel, CardModel)]
      ): CardModel = cardPosition match
        case _: FirstCardSyntacticSugar => cardsOnTable.head._2
        case _: LastCardSyntacticSugar  => cardsOnTable.last._2
        case _ => throw new IllegalArgumentException("Unknown card position")

      extension (cardsOnTable: List[(PlayerModel, CardModel)])
        private def filterByProperty(
            card: CardModel,
            cardPositionProperty: CardFilterSyntacticSugar
        ): List[(PlayerModel, CardModel)] =
          cardPositionProperty match
            case _: SuitSyntacticSugar =>
              cardsOnTable.filter(_._2.suit equals card.suit)
            case _: RankSyntacticSugar =>
              cardsOnTable.filter(_._2.rank equals card.rank)
            case _ =>
              throw new IllegalArgumentException("Unknown card property")
        private def sortByHighestProperty: List[(PlayerModel, CardModel)] =
          highestTakingCardProperty match
            case _: SuitSyntacticSugar =>
              throw new IllegalArgumentException(
                "There is no explicit suit ordering"
              )
            case _: RankSyntacticSugar =>
              cardsOnTable.sortBy(_._2.rank)(using Ordering.Int.reverse)
            case _ =>
              throw new IllegalArgumentException(
                "Unknown card property"
              )

  /** Builder for setting play rules in the GameBuilder.
    *
    * @param highestTakingCardProperty
    *   "suit" or "rank" syntactic sugar
    * @return
    *   a HighestBuilder that can be used to set the play rules
    */
  implicit def highest(
      highestTakingCardProperty: CardFilterSyntacticSugar
  ): HighestBuilder =
    HighestBuilder.apply(highestTakingCardProperty)
