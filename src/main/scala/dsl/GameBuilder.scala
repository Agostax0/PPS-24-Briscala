package dsl

import dsl.types.PlayerCount
import engine.model.PlayerModel

sealed trait GameBuilder:
  val gameName: String
  def addPlayer(name: String): GameBuilder
  def setPlayers(n: Int): GameBuilder
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