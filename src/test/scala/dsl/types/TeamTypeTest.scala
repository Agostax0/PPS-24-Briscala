package dsl.types

import dsl.types.Team
import dsl.types.Team.Team
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import scala.Console.in

class TeamTypeTest
    extends AnyFlatSpec
    with should.Matchers
    with BeforeAndAfterEach:

  private var team: Team = _

  val p1 = "Alice"
  val p2 = "Bob"
  val p3 = "Merk"
  val p4 = "Clark"

  val players = List(p1, p2, p3, p4)

  private def createTeam(): Unit =
    team = Team(List(p1, p2, p3, p4))

  "Team" should "be correctly instantiated" in:
    team = Team(List("Alice", "Bob","Merk", "Clark"))

  it should "have the correct size" in:
      createTeam()

      team.size should be(4)

  it should "convert to a set of players correctly" in:
    createTeam()

    team.toSet should be(players.toSet)

  it should "zip with index correctly" in:
    createTeam()

    team.zipWithIndex should be(players.zipWithIndex)

  it should "reduce correctly" in:
    createTeam()

    team.reduce((a, b) => a + b) should be(p1+p2+p3+p4)

