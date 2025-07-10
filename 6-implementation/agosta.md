# Agosta Alessandro
- lavorato su sintassi DSL + method ordering del builder
- implementazione di mixin di view (card e player)
- implementazione delle play rules

## GameDSL
- scelta nella sintassi (singleton con metodi statici)
- syntax sugar e ssbuilder
- method ordering come estensione del builder (decorator?)

## View Mixins
- CardViewManager
- PlayerViewManager

## Play Rules
A "Play Rule" is a way for the game to know which player is going to win a single turn based on the cards played.  
During the design stage of the play rules, I've followed the team's choice of using a type alias `PlayRule` which internally boils down to a lambda.

Such lambda is structured as follows:
`List[(PlayerModel, CardModel)] => Option[PlayerModel]`

This lambda takes in input the current cards on the playing table, in order of play and referred to which player played them;
it then returns the winning player if the rule is applicable. 

The choice of returning an `Option[PlayerModel]` is derived from the initial requirement of reproducing the "Briscola" game, where two play rules apply:
- The highest ranked card of "briscola" suit wins over the others played
- The highest ranked card of the first played card's suit wins over the other cards

Although these two rules appear straightforward, in many cases they conflict over which player will be awarded the win. 
As such, I've decided to also add a concept of "prevalence" of a rule over another.

Prevalence is used as follows:

`rule prevailsOn anotherRule`

Rule prevalence states that if the `rule` is not applicable then `anotherRule` is to be chosen, otherwise `rule` is to be chosen only. 

This choice allowed to simply implement the briscola's main play rules.

Since a card game could potentially have many rules, the rules are stored as a `List[PlayRule]`.

Due to the unpredictable nature of rules' logic and applicability, I've chosen to limit a turn's winning player to only one which led me to add a check which ensures that distinct applicable rules must not result in distinct winning players.
This check is applied in the following code:
```scala

```

Such cases must be handled using the before mentioned "rule prevalence" concept. 

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
Where one chooses which card position and property, the rule refers to when choosing the prevailing card and the turn-winning player.
