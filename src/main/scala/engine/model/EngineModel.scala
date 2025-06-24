package engine.model

class EngineModel(val gameName: String):
  var players: List[PlayerModel] = List.empty

  def addPlayers(players: List[PlayerModel]): Unit =
    this.players = players
