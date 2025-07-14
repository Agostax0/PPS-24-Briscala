package dsl.types

import dsl.types.Team.Team
import dsl.types.WinRule.lowest
import engine.model.PlayerModel
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import scala.::
import scala.Console.in
import scala.language.postfixOps

class WinRuleTypeTest  extends AnyFlatSpec
  with should.Matchers
  with BeforeAndAfterEach:

  val playerNames = List("Alice", "Bob", "Charlie", "David")

  "A WinRule" should "be correctly instantiated" in:
    val rule: (List[Team], List[PlayerModel]) => List[Team] = (teams, players) => List.empty
    WinRule(rule)

  it should "correctly create a win rule by lowest score" in:
    val team1: Team = Team(List(playerNames(0),playerNames(1)))
    val team2: Team = Team(List(playerNames(2),playerNames(3)))

    val p1 = PlayerModel(playerNames(0))
    val p2 = PlayerModel(playerNames(1))
    p1.score = 10
    p2.score = 0

    val p3 = PlayerModel(playerNames(2))
    val p4 = PlayerModel(playerNames(3))
    p3.score = 9
    p4.score = 0

    given List[Team] = List(team1, team2)

    given List[PlayerModel] = List(p1, p2, p3, p4)

    lowest shouldBe List(team2, team1)