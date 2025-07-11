# Implementation
The task-oriented development methodology ensured that the work was divided fairly among the team members. 
After an initial group phase, during which the project's foundations were laid and some architectural and design details were defined, tasks were assigned based on priority, regardless of the module they belonged to. 
Furthermore, each task encompassed entire features of the application (from implementation in the DSL to display in the view, including testing), so every team member worked on all sections of the project. 
Below are the individual contributions of each member.

- [Agosta Alessandro](./agosta.md)
- [Alni Riccardo](./alni.md)
- [Pesic Marco](./pesic.md)

## Tri-Programming
In this section a description of our group-work will be provided. 
### GameBuilder
In order to construct a correct and ready-for-use GameModel of the game, we've decided to rely on the builder pattern.
For this reason we developed the `GameBuilder` trait, which contains methods to build a fully-functional `FullEngineModel`.
During configuration with the DSL, input parameters are checked and saved internally in the builder, to ultimately call the `build()` method which returns a `FullEngineModel` representation.   

Not all semantic checks on the input data are done by the `GameBuilder`, but this task is delegated to each type alias representing an attribute. 

For example, we have chosen to limit a game's number of players to an interval between two and four. 
```scala
object PlayerCount:
  opaque type PlayerCount = Int
  def apply(count: Int): PlayerCount =
    require(count >= 2 && count <= 4, "player count should be between 2 and 4")
    count
```

During an initial meeting, we chose which game's features must be initialized and which were optional; we concluded that these are mandatory:
- the game's name
- the number of players
- the list of players
- the suits of the deck
- the ranks of the deck
- the cards to give to each player

With these features, a complete and ready-to-play game can be built.

Additionally, these rules are optional, and as such, default values are used if not overridden:
- the starting player
- the point rules
- the win rule
- the composition of teams
- the play rules
- the hand rule
- the briscola suit

### Bot
A Bot is recognized as any other player, being an extension of the `PlayerModel` trait by using scala's `export` keyword, which applied the delegation pattern.
```scala

sealed trait BotPlayerModel extends PlayerModel
object BotPlayerModel: 
  ...
  private class BotPlayerModelImpl(
      val player: PlayerModel,
      val botType: BotType
  ) extends BotPlayerModel:
    export player.*
    ...
```
Upon creation, a bot also needs a `BotType` which refers to the bot behavior, applied using the strategy pattern, for choosing a card to play.
The currently implemented strategies are:
- `RandomStrategy` in which a bot chooses randomly among its playable cards in hand
- `RuleAwareDecisionStrategy` in which a bot checks if any held cards are eligible to win the current turn, among these it chooses the least valuable; if no cards would win then the bot would choose the least score-giving card. 
### PointRule 
    

| [Previous Chapter](../5-detailed_design/index.md) | [Index](../index.md) | [Next Chapter](../7-testing/index.md) |