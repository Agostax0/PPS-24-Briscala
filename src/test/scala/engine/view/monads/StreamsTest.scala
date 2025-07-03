package engine.view.monads

import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import engine.view.monads.Streams.*

class StreamsTest
    extends AnyFlatSpec
    with should.Matchers
    with BeforeAndAfterEach:

  var stream: Stream[Int] = Stream.cons(1, Stream.cons(2, Stream.cons(3, Stream.empty())))

  override def beforeEach(): Unit =
    stream = Stream.cons(1, Stream.cons(2, Stream.cons(3, Stream.empty())))

  "Streams" should "convert to a list" in:
    val list = stream.toList
    list shouldBe Sequence.Cons(1, Sequence.Cons(2, Sequence.Cons(3, Sequence.Nil())))

  it should "map over the elements" in:
    val mappedStream = stream.map(_ * 2).toList
    mappedStream shouldBe Sequence.Cons(2, Sequence.Cons(4, Sequence.Cons(6, Sequence.Nil())))

  it should "filter elements" in:
    val filteredStream = stream.filter(_ % 2 == 0).toList
    filteredStream shouldBe Sequence.Cons(2, Sequence.Nil())

  it should "take a specified number of elements" in:
    val takenStream = stream.take(2).toList
    takenStream shouldBe Sequence.Cons(1, Sequence.Cons(2, Sequence.Nil()))

  it should "generate an infinite stream of elements" in:
    val infiniteStream = Stream.generate(() => 1).take(5).toList
    infiniteStream shouldBe Sequence.Cons(1, Sequence.Cons(1, Sequence.Cons(1, Sequence.Cons(1, Sequence.Cons(1, Sequence.Nil())))))
