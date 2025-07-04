package examples

import dsl.{GameBuilder, GameDSL}
import dsl.GameDSL.{game, is, *}
import dsl.syntax.SyntacticSugar.*
import dsl.syntax.SyntacticSugarBuilder.highest
import dsl.types.HandRule.*
import dsl.types.PlayRule.prevailsOn
import dsl.types.Team
import dsl.types.Team.Team
import dsl.types.WinRule.highest as highestPointTeam
import engine.model.{CardModel, DeckModel, PlayerModel}

import scala.language.{implicitConversions, postfixOps}
object GameExamples:
  def briscola(): GameBuilder =
    game is "Briscola"
    game has 4 players
  
    game has player called "Alice"
    game has player called "Bob"
    game has smartBot called "Albert"
    game has randomBot called "Josh"
  
    game suitsAre("Cups", "Coins", "Swords", "Batons")
    game ranksAre("2", "4", "5", "6", "7", "Knave", "Knight", "King", "3", "Ace")
    game gives 3 cards to every player
  
    game firstTurn starts from "Alice"
  
    game briscolaIs "Cups"
  
    game card points are :
      (name, suit) =>
        name match
          case "Ace" => 11
          case "3" => 10
          case "King" => 4
          case "Knight" => 3
          case "Knave" => 2
          case _ => 0
  
    game play rules are :
      val highestBriscolaTakesRule = (cards: List[(PlayerModel, CardModel)]) =>
        given List[(PlayerModel, CardModel)] = cards
  
        highest(suit) that takes is briscolaSuit
  
      val highestCardTakesRule = (cards: List[(PlayerModel, CardModel)]) =>
        given List[(PlayerModel, CardModel)] = cards
  
        highest(rank) that takes follows first card suit
  
      highestBriscolaTakesRule prevailsOn highestCardTakesRule
  
    game win rules is :
      (teams, listOfPlayers) =>
        given List[Team] = teams
  
        given List[PlayerModel] = listOfPlayers
  
        highestPointTeam
  
    game
  
  def marafone(): GameBuilder =
    game is "Marafone"
    game has 4 players
  
    game has player called "Alice"
    game has player called "Bob"
    game has player called "Bob1"
    game has player called "Bob2"
  
    game has team composedOf("Alice", "Bob")
    game has team composedOf("Bob1", "Bob2")
  
    game suitsAre("Cups", "Coins", "Swords", "Batons")
    game ranksAre("4", "5", "6", "7", "Knave", "Knight", "King", "Ace", "2", "3")
    game gives 10 cards to every player
  
    game firstTurn starts from "Alice"
  
    game briscolaIs "Cups"
  
    game card points are:
      (name, suit) =>
        name match
          case "Ace" => 10
          case "3" | "2"| "King" | "Knight" | "Knave" => 3
          case _ => 0
  
    game hand rules are:
      (cardsOnTable, playerHand, playedCard) =>
        given List[CardModel] = cardsOnTable
        given DeckModel = playerHand
        given CardModel = playedCard
  
        freeStart or followFirstSuit
        //marafoneRuleset
  
    game play rules are:
      val highestBriscolaTakesRule = (cards: List[(PlayerModel, CardModel)]) =>
        given List[(PlayerModel, CardModel)] = cards
  
        highest(suit) that takes is briscolaSuit
  
      val highestCardTakesRule = (cards: List[(PlayerModel, CardModel)]) =>
        given List[(PlayerModel, CardModel)] = cards
  
        highest(rank) that takes follows first card suit
  
      highestBriscolaTakesRule prevailsOn highestCardTakesRule
  
    game win rules is:
      (teams, listOfPlayers) =>
        given List[Team] = teams
        given List[PlayerModel] = listOfPlayers
  
        highestPointTeam
  
    game
  
  def custom(): GameBuilder =
    game is "Briscala"
    game has 4 players
  
    game has player called "Alice"
    game has smartBot called "SmartBot"
    game has player called "Bob"
    game has player called "Josh"
  
    game suitsAre("Cups", "Coins", "Swords", "Batons")
    game ranksAre("Ace", "2", "3", "4", "5", "6", "7", "Knave", "Knight", "King")
    game gives 7 cards to every player
  
    game firstTurn starts from "Alice"
  
    game briscolaIs "Cups"
  
    game hand rules are :
      (cardsOnTable, playerHand, playedCard) =>
        given List[CardModel] = cardsOnTable
  
        given DeckModel = playerHand
  
        given CardModel = playedCard
  
        startWithHigherCard or followPreviousSuit
  
    game card points are :
      ((name, suit) =>
        name match
          case "Ace" => 5
          case "King" => 2
          case "Knight" => 2
          case "Knave" => 2
          case _ => 0
        , (name, suit) =>
          suit match
            case "Coins" => 1
            case _ => 0
      )
  
    game play rules are :
      val highestBriscolaTakesRule = (cards: List[(PlayerModel, CardModel)]) =>
        given List[(PlayerModel, CardModel)] = cards
  
        highest(suit) that takes is briscolaSuit
  
      val highestCardTakesRule = (cards: List[(PlayerModel, CardModel)]) =>
        given List[(PlayerModel, CardModel)] = cards
  
        highest(rank) that takes follows last card suit
  
      highestBriscolaTakesRule prevailsOn highestCardTakesRule
  
    game win rules is :
      (teams, listOfPlayers) =>
        given List[Team] = teams
  
        given List[PlayerModel] = listOfPlayers
  
        highestPointTeam
  
    game