package dsl

object GameDSL:
  def apply(gameBuilder: GameBuilder): Unit = builder = gameBuilder
  private var builder: GameBuilder = GameBuilder("")
  implicit def game: GameBuilder = builder
