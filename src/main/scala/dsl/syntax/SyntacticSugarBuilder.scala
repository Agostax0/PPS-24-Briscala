package dsl.syntax

import dsl.GameBuilder
import dsl.syntax.SyntacticSugar.*
import dsl.types.{HandRule, PlayRule, PointsRule}
import dsl.syntax.SyntacticSugar.{PlayerSyntacticSugar, ToSyntacticSugar}
import dsl.types.{HandRule, PlayRule, PointsRule, Team, WinRule}
import engine.model.{CardModel, DeckModel, PlayerModel}

import scala.language.implicitConversions

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
        pointRules.foreach(rule => builder.setPointRule(PointsRule(rule)))
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
        builder.setHandRule(HandRule(handRules))
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
        playRules.foreach(rule => builder.setPlayRule(PlayRule(rule)))
        builder

  trait WinRulesBuilder:
    infix def is(
        winRules: (List[Team], List[PlayerModel]) => List[Team]
    ): GameBuilder

  object WinRulesBuilder:
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

  trait HighestBuilder:
    infix def that(takes: TakesSyntacticSugar): HighestCardBuilder

  private object HighestBuilder:
    def apply(filterSyntacticSugar: CardFilterSyntacticSugar): HighestBuilder =
      new HighestBuilderImpl(filterSyntacticSugar)
    private class HighestBuilderImpl(
        val highestTakingCardProperty: CardFilterSyntacticSugar
    ) extends HighestBuilder:
      override infix def that(takes: TakesSyntacticSugar): HighestCardBuilder =
        HighestCardBuilder.apply(highestTakingCardProperty)

  trait HighestCardBuilder:
    infix def is(suit: String)(using
        List[(PlayerModel, CardModel)]
    ): Option[PlayerModel]
    infix def follows(
        cardPosition: CardPositionSyntacticSugar
    ): HighestCardOfCardPlayedBuilder
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
      ): HighestCardOfCardPlayedBuilder =
        HighestCardOfCardPlayedBuilder(filterSyntacticSugar, cardPosition)

      override infix def is(briscola: String)(using
          cardsOnTable: List[(PlayerModel, CardModel)]
      ): Option[PlayerModel] =
        val winner = cardsOnTable
          .filter(_._2.suit equals briscola)
          .sortBy(_._2.rank)(using Ordering.Int.reverse)
          .map(_._1)
          .headOption
        winner
  trait HighestCardOfCardPlayedBuilder:
    infix def card(suit: CardFilterSyntacticSugar)(using
        List[(PlayerModel, CardModel)]
    ): Option[PlayerModel]

  private object HighestCardOfCardPlayedBuilder:
    def apply(
        highestTakingCardProperty: CardFilterSyntacticSugar,
        cardPosition: CardPositionSyntacticSugar
    ): HighestCardOfCardPlayedBuilder =
      new HighestCardOfCardPlayedBuilderImpl(
        highestTakingCardProperty,
        cardPosition
      )
    private class HighestCardOfCardPlayedBuilderImpl(
        val highestTakingCardProperty: CardFilterSyntacticSugar,
        val cardPosition: CardPositionSyntacticSugar
    ) extends HighestCardOfCardPlayedBuilder:
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
        case _ => throw new IllegalArgumentException("Illegal card position")

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
              throw new IllegalArgumentException("Illegal card property")
        private def sortByHighestProperty: List[(PlayerModel, CardModel)] =
          highestTakingCardProperty match
            case _: SuitSyntacticSugar =>
              throw new IllegalArgumentException(
                "There is no explicit suit ordering"
              )
            case _: RankSyntacticSugar =>
              cardsOnTable.sortBy(_._2.rank)(using Ordering.Int.reverse)
            case _ =>
              throw new IllegalArgumentException("Illegal card property")
  implicit def highest(
      highestTakingCardProperty: CardFilterSyntacticSugar
  ): HighestBuilder =
    HighestBuilder.apply(highestTakingCardProperty)
