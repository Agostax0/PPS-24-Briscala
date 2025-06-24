package dsl

import dsl.types.PlayerCount
import engine.model.{EngineModel, PlayerModel}

sealed trait GameBuilder:
  val gameName: String
  def addPlayer(name: String): GameBuilder
  def setPlayers(n: Int): GameBuilder
  def build(): EngineModel

object GameBuilder:
  def apply(gameName: String): GameBuilder = GameBuilderImpl(gameName)

  private class GameBuilderImpl(val gameName: String) extends GameBuilder:
    private var players: List[PlayerModel] = List.empty
    private var playerCount: PlayerCount = _

    override def addPlayer(name: String): GameBuilder =
      players = players :+ PlayerModel(name)
      this

    override def setPlayers(n: Int): GameBuilder =
      playerCount = PlayerCount(n)
      this

    override def build(): EngineModel =
      if !playerCount.equals(PlayerCount(players.size)) then
        throw new IllegalArgumentException("Incorrect number of players joined")

      val game = EngineModel(gameName)
      game.addPlayers(players)
      game
