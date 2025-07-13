# Requirements

## Business Requirements
* The DSL should allow users to create card games using a simple and intuitive syntax.
* The game engine will allow the users to play the card game created with the DSL through a GUI.

## Functional Requirements
* Below are the main requirements of the common base of the games:
  * Players (from 2 to 4) draw a certain number of card from a shared deck and keep them in their hands
  * Each player takes turn playing a card from their hand and placing it on the table
  * After all players have completed their turn, the outcome of the turn is calculated, the table is cleared and the game proceeds to the next turn
  * If the deck still contains enough cards, players draw to return to their initial hand size; otherwise, if the deck is empty, the game continues without drawing new cards 
  * The games ends when there are no more cards in the players' hands.
### User Functional Requirements
* The user should be able to:
  * Define the rules of the game using the DSL:
    * Choose the number of players
    * Define the deck composition (suits, ranks and points)
    * Choose the briscola suit
    * Set restrictions on card plays, like requiring to players to follow the first suit played in a turn
    * Define the turn winning condition
    * Define the game winning condition
  * Play the game using a GUI:
    * View the current state of the game
    * Play a card from their hand
    * See the outcome of the turn
    * See the outcome of the game

### System Functional Requirements
* The system should be able to:
  * Usage of the DSL should ensure game creation and playability
  * Handle the game logic, including turn management, card drawing, specific rules and win conditions
  * Provide a GUI for users to interact with the game

## Non-Functional Requirements
* Readability: the DSL should be easy to read and understand, allowing users to quickly grasp the game rules
* Modularity: the DSL should allow users to define different game rules and configurations without modifying the core engine
* Performance: the game engine should be efficient and responsive, providing a smooth user experience
* Robustness: the game engine should enforce correctness in input data and notify when malformed.

## Implementation Requirements
* Use of Scala 3.+
* Use of JDK 21+
* The DSL engine should be implemented as an Internal DSL
* The DSL should use Scala's features and syntax to create an almost natural language-like game configuration
* The codebase should be well-organized, documented and maintained throughout its development
* The project must, first and foremost run on Linux, optionally it could run on other operating systems.

## Optional Requirements
* The following requirements are not mandatory to for project completion, but their implementation would improve the quality of the result:
  * Team-based play
  * Single play against bots
  * The application should enforce the correct usage of the DSL to the user, preventing the creation of invalid game configurations
  * Extension of the engine to support more complex games in which the table is not cleared at the end of each turn

| [Previous Chapter](../2-development_process/index.md) | [Index](../index.md) | [Next Chapter](../4-architectural_design/index.md) |
