# Agosta Alessandro
In this project I'm responsible for the DSL and its ordering, some view components namely: CardView and PlayerView, and the Play Rules.

I've primarily worked on the following files: GameDSL, OrderedGameBuilder, CardViewManager, PlayerViewManager, GameElements(specifically on PlayRule).
## GameDSL
As per the requirements, the game creation engine must feature a DSL, to be used through simple and intuitive syntax as a configuration tool.

My idea was for the `GameDSL` to provide a more natural-like syntax for interacting with the `GameBuilder` class; in order to ultimately provide a complete `GameModel` ready for use.
### DSL Development
DSL development followed these steps:
- a syntax was created for a needed game configuration
- such syntax was reviewed with colleagues for understandability
- then such configuration was introduced into the GameBuilder
- finally the DSL built the aforementioned syntax to correctly relay the configuration to the GameBuilder

As to the design choices:

- the DSL provides natural language-like extension methods to interact with the GameBuilder trait.
- defines a getter method `game` which the user can use to refer to the game, in order to add configurations.
- delegates complex syntaxes to specific-configuration builders, tasked with providing syntactic sugar.

Since the DSL has only a utility role and its usage can be avoided if bypassed by using directly a GameBuilder, I chose to make it a Singleton object with static extension methods for the GameBuilder.

The DSL holds internally a GameBuilder, accessible with the `game` method, to which it relies on to build a complete GameModel.
```scala
object GameDSL:
  private var builder: GameBuilder = _
  implicit def game: GameBuilder = builder
```
It then offers extension methods for a "seamless" language-like syntax:
```scala
object GameDSL:
  ...
  extension (gameBuilder: GameBuilder)
    infix def is(name: String): GameBuilder =
      builder = OrderedGameBuilder(name, GameBuilder(name))
      builder
```
Which allows a configuration syntax as such:
```scala
game is "Briscola"
```
Each extension method was also designed such that dotted method call could be avoided, but still available.
```scala
game is "Briscola"
game has 4 players
game has player called "Alice"

game.is("Briscola")
  .has(4).players
  .has(player).called("Alice")
```

As mentioned before, complex syntax is delegated from the DSL to specific-builders (which in turn may call other builders) using the fluent pattern.
For example:
```scala
game has player called "Alice"

val entityBuilder: EntityBuilder = game has player
val gameBuilder: GameBuilder = entityBuilder called "Alice"
```
Such builders are called `SyntaxBuilder` and use implicit variables called `SyntaxSugar` which allow for seamless language-like syntax.
In the previous example `player` is a SyntaxSugar variable.
## Method Ordering
Just like in any language, the DSL is not immune to errors; in particular it's prone to semantic errors. 

For example, the DSL sees no problem in configuring which player the game starts from, but players may have not been defined yet.
In order to resolve this issue, I've modified the DSL to use a `OrderedGameBuilder` instead of a `GameBuilder`.

The `OrderedGameBuilder` is a decorator of a `GameBuilder` which is tasked with ensuring the correct method's order of call.
```scala
trait OrderedGameBuilder extends GameBuilder
```
In this trait's hidden implementation, each builder's method has a mandatory order of call.

Initially, method ordering was thought to be at compile-time, by using phantom types, but its implementation would've exceeded the time constraints.
For simplicity, it was decided to have this order check at runtime.

`OrderedGameBuilder` relies upon `BuilderStep`, an enum which lists all GameBuilder steps', and each next step.

## View Mixins
- CardViewManager
- PlayerViewManager

## Play Rules
A "Play Rule" is a way for the game to know which player is going to win a turn based on the cards played.  
During the design stage of the play rules, I've followed the team's choice of using a type alias `PlayRule` which internally converts to a lambda.

Such lambda is structured as follows:
```scala
List[(PlayerModel, CardModel)] => Option[PlayerModel]
```
This lambda takes in input the current cards on the playing table, in order of play and linked to which player played them;
it then returns the winning player if the rule is applicable. 

The choice of returning an `Option[PlayerModel]` is derived from the initial requirement of reproducing the "Briscola" game, where two play rules apply:
- The highest ranked card of "briscola" suit wins over the others played
- The highest ranked card of the first played card's suit wins over the other cards

Although these two rules appear straightforward, in many cases they conflict over which player will be awarded the win. 
As such, I've decided to also add a concept of "prevalence" of a rule over another.

Prevalence is used as follows:

```scala
rule prevailsOn anotherRule
```

Rule prevalence states that if the `rule` is not applicable then `anotherRule` is to be chosen, otherwise `rule` is to be chosen only. 

This choice allowed to simply implement the briscola's main play rules.

Since a card game could potentially have many rules, the rules are stored as a `List[PlayRule]`.

Due to the unpredictable nature of rules' logic and applicability, I've chosen to limit a turn's winning player to only one which led me to add a check which ensures that distinct applicable rules must not result in distinct winning players; such cases must be handled using the before mentioned "rule prevalence" concept.

This check is applied in the following code:
```scala
def calculateWinningPlayer(
    cardsOnTableByPlayer: List[(PlayerModel, CardModel)]
): Option[PlayerModel] =
  val winningPlayers: List[PlayerModel] =
    playRules
      .map(rule => rule(cardsOnTableByPlayer))
      .filter(_.isDefined)
      .map(_.get)

  if winningPlayers.size > 1 then return None
  winningPlayers.headOption
```
### Play Rules syntax

A game play rules' are unpredictable in nature and as such I've decided to allow the user to also configure them by also using a custom DSL; which allows:
- Setting the winning card to the highest ranked card of "briscola" suit 
- Setting the winning card to be the highest ranked card of the first played card's suit

Although, due to time constraints, this DSL is minimal and only allows the user to define play rules using the following syntax:
```scala
val highestBriscolaTakesRule = (cards: List[(PlayerModel, CardModel)]) =>
  given List[(PlayerModel, CardModel)] = cards
  highest(suit) that takes is briscolaSuit

val highestCardTakesRule = (cards: List[(PlayerModel, CardModel)]) =>
  given List[(PlayerModel, CardModel)] = cards
  highest(rank) that takes follows first card suit
```
And such rules are applied to a game using the following syntax:
```scala
game play rules are :
  highestBriscolaTakesRule prevailsOn highestCardTakesRule
```

It's worth to mention that the DSL allows some degree of freedom when choosing the winning card, for example in these are valid syntaxes:
```scala
val exampleRule1 = (cards: List[(PlayerModel, CardModel)]) =>
  given List[(PlayerModel, CardModel)] = cards
  highest(rank) that takes follows first card suit

val exampleRule2 = (cards: List[(PlayerModel, CardModel)]) =>
  given List[(PlayerModel, CardModel)] = cards
  highest(rank) that takes follows last card suit

val exampleRule3 = (cards: List[(PlayerModel, CardModel)]) =>
  given List[(PlayerModel, CardModel)] = cards
  highest(rank) that takes follows first card rank

val exampleRule4 = (cards: List[(PlayerModel, CardModel)]) =>
  given List[(PlayerModel, CardModel)] = cards
  highest(rank) that takes follows last card rank
```
Where one chooses which card position (first or last) and property (same rank or same suit), the rule refers to when choosing the prevailing card and the turn-winning player.
