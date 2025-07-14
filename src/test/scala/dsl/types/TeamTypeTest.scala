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

  val players: List[String] = List(p1, p2, p3, p4)

  override def beforeEach(): Unit =
    team = Team(List(p1, p2, p3, p4))

  "Team" should "be correctly instantiated" in:
    team = Team(List("Alice", "Bob","Merk", "Clark"))

  it should "have the correct size" in:
      team.size should be(4)

  it should "convert to a set of players correctly" in:
    team.toSet should be(players.toSet)

  it should "zip with index correctly" in:
    team.zipWithIndex should be(players.zipWithIndex)

  it should "reduce correctly" in:
    team.reduce((a, b) => a + b) should be(p1+p2+p3+p4)

  it should "be able to check if it contains a player" in:
    (team.contains(p1), team.contains("Unknown")) shouldBe (true, false)

  it should "recognize another team as equal if they have the same players" in:
    val team2: Team = Team(List(p1, p2, p3, p4))

    team.equals(other = team2) should be(true)