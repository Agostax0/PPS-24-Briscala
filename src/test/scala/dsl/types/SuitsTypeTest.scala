package dsl.types

import dsl.types.Suits
import dsl.types.Suits.Suits
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import scala.Console.in
class SuitsTypeTest
    extends AnyFlatSpec
    with should.Matchers
    with BeforeAndAfterEach:


  var suits: Suits = _

  val cups = "Cups"
  val coins = "Coins"
  val swords = "Swords"
  val batons = "Batons"
  List("Cups", "Coins", "Swords", "Batons")
  val suitsList = List(cups, coins, swords, batons)

  private def createSuits(): Unit =
    suits = Suits(suitsList)

  "Suits" should "be correctly instantiated" in:
    suits = Suits(List("Cups", "Coins", "Swords", "Batons"))

  it should "have distinct suits" in:
    createSuits()

    suits.distinct should be(suitsList.distinct)
    suits.size should be(suitsList.size)

  it should "be mapped correctly" in:
    createSuits()

    suits.map(_.toUpperCase) should be(suitsList.map(_.toUpperCase))

  it should "be flatMapped correctly" in:
    createSuits()

    suits.flatMap(List(_)) should be(suitsList.flatMap(List(_)))