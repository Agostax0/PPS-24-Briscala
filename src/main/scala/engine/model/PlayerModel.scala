package engine.model

sealed trait PlayerModel:
  val name: String
  //val hand: Nothing = ??? //Deck = ???
  val score: Int = 0
object PlayerModel:
  def apply(name: String): PlayerModel = PlayerModelImpl(name)

  private class PlayerModelImpl(val name: String) extends PlayerModel {}
