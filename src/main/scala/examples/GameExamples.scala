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

  /** Creates a game builder for the game "Briscola".
   * The game is set up with 2 players, Alice and Bob, and includes two bots: Albert (smart) and Josh (random).
   * The game uses the standard deck and rules of "Briscola". The set briscola suit is "Cups".
   *
   * @return the GameBuilder for the game
   */
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

  /** Creates a game builder for the game "Marafone".
   * The game is set up with 4 players: Alice, Bob, Bob1, and Bob2.
   * It includes two teams: Alice and Bob on one team, and Bob1 and Bob2 on the other.
   * The set briscola suit is "Cups".
   * The game is a variation of "Briscola" with specific rules and card points, most
   * notably the rule that a player must follow the first suit played if possible.
   *
   * @return
   */
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

  /** Creates a custom game called "Briscala".
   * This game is set up with 3 players: Alice, Bob, and Josh and a smart bot named SmartBot.
   * It uses the same deck of briscola and the set briscola suit is "Cups".
   * The specific rules are:
   *  - The starting player must play its highest rank card
   *  - Players must follow the previous suit if possible
   *  - The ace is the most valuable card with 5 points, followed by King, Knight, and Knave with 2 points each.
   *  - Cards of suit "Coins" are worth 1 extra point.
   *  - The card ranks follow the numerical order from Ace to 7, followed by Knave, Knight, and King.
   *  - The winning turn is determined by the highest card of the last suit played, unless a briscola is played.
   *  - The player with the highest points at the end of the game wins.
   *
   * @return
   */
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