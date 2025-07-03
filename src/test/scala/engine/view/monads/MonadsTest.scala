package engine.view.monads

import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import engine.view.monads.Streams.*

object Optionals:

  // data structure for optionals
  enum Optional[A]:
    case Just(a: A)
    case Empty()

  // minimal set of algorithms
  object Optional:
    extension [A](m: Optional[A])
      def filter(p: A => Boolean): Optional[A] = m match
        case Just(a) if (p(a)) => m
        case _                 => Empty()

class MonadsTest
    extends AnyFlatSpec
    with should.Matchers
    with BeforeAndAfterEach:
  import Monads.*
  import Optionals.*
  import Optional.{Just, Empty}

  given Monad[Optional] with
    import Optional.{Just, Empty}

    // unit: just boxing the value
    def unit[A](a: A): Optional[A] = Just(a)

    // flatMap: opens the box if possible, gives the new box
    extension [A](m: Optional[A])
      def flatMap[B](f: A => Optional[B]): Optional[B] =
        m match
          case Just(a) => f(a)
          case Empty() => Empty()

  "Monads" should "wrap an Optional with unit" in:
    val optional: Optional[Int] = summon[Monad[Optional]].unit(10)
    optional shouldBe Just(10)

  it should "apply flatMap to an Optional" in:
    val optional: Optional[Int] = Just(10)
    val result: Optional[String] = optional.flatMap(a => Just(a + "0"))
    result shouldBe Just("100")

  it should "apply map to an Optional" in:
    val optional: Optional[Int] = Just(10)
    val result: Optional[Int] = optional.map(_ + 0)
    result shouldBe Just(10)

  it should "return Empty when flatMap is applied to Empty" in:
    val optional: Optional[Int] = Empty()
    val result: Optional[String] = optional.flatMap(a => Just(a + "0"))
    result shouldBe Empty()

  it should "combine two Optionals with map2" in:
    val optional1: Optional[Int] = Just(10)
    val optional2: Optional[Int] = Just(20)
    val result: Optional[Int] = Monad.map2(optional1, optional2)(_ + _)
    result shouldBe Just(30)

  it should "return Empty when one of the Optionals is Empty in map2" in:
    val optional1: Optional[Int] = Just(10)
    val optional2: Optional[Int] = Empty()
    val result: Optional[Int] = Monad.map2(optional1, optional2)(_ + _)
    result shouldBe Empty()

  it should "return the second Optional in seq" in:
    val optional1: Optional[Int] = Just(10)
    val optional2: Optional[Int] = Just(20)
    val result: Optional[Int] = Monad.seq(optional1, optional2)
    result shouldBe Just(20)

  it should "return Empty when the first Optional is Empty in seq" in:
    val optional1: Optional[Int] = Empty()
    val optional2: Optional[Int] = Just(20)
    val result: Optional[Int] = Monad.seq(optional1, optional2)
    result shouldBe Empty()

  it should "process a stream of Optionals with seqN" in:
    val stream: Stream[Optional[Int]] =
      Stream.Cons(
        () => Just(1),() =>
        Stream.Cons(
          () => Just(2), () =>
          Stream.Cons(() => Just(3), () =>
            Stream.Empty()
          )
        )
      )
    val result: Optional[Int] = Monad.seqN(stream)
    result shouldBe Just(3)

  it should "throw an exception when the stream is empty in seqN" in:
    val emptyStream: Stream[Optional[Int]] = Stream.Empty()
    a [NoSuchElementException] should be thrownBy Monad.seqN(emptyStream)
