package org.scalatest.prop

import org.scalatest.prop.Configuration.PropertyCheckConfiguration
import org.scalatest.{FailureMessages, UnquotedString, _}
import org.scalatest.exceptions.DiscardedEvaluationException

import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

trait PropertyFun { f: Configuration =>

  def checkFor1[A, ASSERTION](names: List[String], config: PropertyCheckConfiguration)(fun: (A) => ASSERTION)
                                   (implicit
                                    genA: org.scalatest.prop.Generator[A]
                                   ): PropertyFun.Result = {
    val maxDiscarded = PropertyCheckConfiguration.calculateMaxDiscarded(config.maxDiscardedFactor, config.minSuccessful)
    val maxSize = config.minSize + config.sizeRange

    @tailrec
    def loop(succeededCount: Int, discardedCount: Int, nextRandomizer: Randomizer, initialSizes: List[Int]): PropertyFun.Result = {
      val (size, nextInitialSizes, nextRandomizer2) =
        initialSizes match {
          case head :: tail => (head, tail, nextRandomizer)
          case Nil =>
            val (sz, r2) = nextRandomizer.chooseInt(config.minSize, maxSize)
            (sz, Nil, r2)
        }
      val (a, ar) = genA.next(10, nextRandomizer)

      val result: Try[Unit] = Try { fun(a) }
      val argsPassed = List(a)
      result match {
        case Success(()) =>
          val nextSucceededCount = succeededCount + 1
          if (nextSucceededCount < config.minSuccessful)
            loop(nextSucceededCount, discardedCount, ar, nextInitialSizes)
          else
            PropertyFun.Success
        case Failure(ex: DiscardedEvaluationException) =>
          val nextDiscardedCount = discardedCount + 1
          if (nextDiscardedCount < maxDiscarded)
            loop(succeededCount, nextDiscardedCount, ar, nextInitialSizes)
          else
            new PropertyFun.Exhausted(succeededCount, nextDiscardedCount)
        case Failure(ex) =>
          new PropertyFun.Failure(succeededCount, ex, names, argsPassed)
      }
    }

    @tailrec
    def sizesLoop(sizes: List[Int], count: Int, rnd: Randomizer): List[Int] = {
      sizes match {
        case Nil => sizesLoop(List(config.minSize), 1, rnd)
        case szs if count < 10 =>
          val (nextSize, nextRandomizer) = rnd.chooseInt(config.minSize, maxSize)
          sizesLoop(nextSize :: sizes, count + 1,  nextRandomizer)
        case _ => sizes.sorted
      }
    }
    val initialSizes = sizesLoop(Nil, 0, Randomizer.default)
    loop(0, 0, Randomizer.default, initialSizes)
  }

  def checkFor2[A, B, ASSERTION](names: List[String], config: PropertyCheckConfiguration)(fun: (A, B) => ASSERTION)
                                      (implicit
                                       genA: org.scalatest.prop.Generator[A],
                                       genB: org.scalatest.prop.Generator[B]
                                      ): PropertyFun.Result = {
    val maxDiscarded = PropertyCheckConfiguration.calculateMaxDiscarded(config.maxDiscardedFactor, config.minSuccessful)
    val maxSize = config.minSize + config.sizeRange

    @tailrec
    def loop(succeededCount: Int, discardedCount: Int, nextRandomizer: Randomizer, initialSizes: List[Int]): PropertyFun.Result = {
      val (size, nextInitialSizes, nextRandomizer2) =
        initialSizes match {
          case head :: tail => (head, tail, nextRandomizer)
          case Nil =>
            val (sz, r2) = nextRandomizer.chooseInt(config.minSize, maxSize)
            (sz, Nil, r2)
        }
      val (a, ar) = genA.next(10, nextRandomizer)
      val (b, br) = genB.next(10, ar)
      val result: Try[Unit] = Try { fun(a, b) }
      val argsPassed = List(a, b)
      result match {
        case Success(()) =>
          val nextSucceededCount = succeededCount + 1
          if (nextSucceededCount < config.minSuccessful)
            loop(nextSucceededCount, discardedCount, br, nextInitialSizes)
          else
            PropertyFun.Success
        case Failure(ex: DiscardedEvaluationException) =>
          val nextDiscardedCount = discardedCount + 1
          if (nextDiscardedCount < maxDiscarded)
            loop(succeededCount, nextDiscardedCount, br, nextInitialSizes)
          else
            new PropertyFun.Exhausted(succeededCount, nextDiscardedCount)
        case Failure(ex) =>
          new PropertyFun.Failure(succeededCount, ex, names, argsPassed)
      }
    }

    @tailrec
    def sizesLoop(sizes: List[Int], count: Int, rnd: Randomizer): List[Int] = {
      sizes match {
        case Nil => sizesLoop(List(config.minSize), 1, rnd)
        case szs if count < 10 =>
          val (nextSize, nextRandomizer) = rnd.chooseInt(config.minSize, maxSize)
          sizesLoop(nextSize :: sizes, count + 1,  nextRandomizer)
        case _ => sizes.sorted
      }
    }
    val initialSizes = sizesLoop(Nil, 0, Randomizer.default)
    loop(0, 0, Randomizer.default, initialSizes)
  }

  def checkFor3[A, B, C, ASSERTION](names: List[String], config: PropertyCheckConfiguration)(fun: (A, B, C) => ASSERTION)
                                         (implicit
                                          genA: org.scalatest.prop.Generator[A],
                                          genB: org.scalatest.prop.Generator[B],
                                          genC: org.scalatest.prop.Generator[C]
                                         ): PropertyFun.Result = {
    val maxDiscarded = PropertyCheckConfiguration.calculateMaxDiscarded(config.maxDiscardedFactor, config.minSuccessful)
    val maxSize = config.minSize + config.sizeRange

    @tailrec
    def loop(succeededCount: Int, discardedCount: Int, nextRandomizer: Randomizer, initialSizes: List[Int]): PropertyFun.Result = {
      val (size, nextInitialSizes, nextRandomizer2) =
        initialSizes match {
          case head :: tail => (head, tail, nextRandomizer)
          case Nil =>
            val (sz, r2) = nextRandomizer.chooseInt(config.minSize, maxSize)
            (sz, Nil, r2)
        }
      val (a, ar) = genA.next(10, nextRandomizer)
      val (b, br) = genB.next(10, ar)
      val (c, cr) = genC.next(10, br)
      val result: Try[Unit] = Try { fun(a, b, c) }
      val argsPassed = List(a, b, c)
      result match {
        case Success(()) =>
          val nextSucceededCount = succeededCount + 1
          if (nextSucceededCount < config.minSuccessful)
            loop(nextSucceededCount, discardedCount, cr, nextInitialSizes)
          else
            PropertyFun.Success
        case Failure(ex: DiscardedEvaluationException) =>
          val nextDiscardedCount = discardedCount + 1
          if (nextDiscardedCount < maxDiscarded)
            loop(succeededCount, nextDiscardedCount, cr, nextInitialSizes)
          else
            new PropertyFun.Exhausted(succeededCount, nextDiscardedCount)
        case Failure(ex) =>
          new PropertyFun.Failure(succeededCount, ex, names, argsPassed)
      }
    }

    @tailrec
    def sizesLoop(sizes: List[Int], count: Int, rnd: Randomizer): List[Int] = {
      sizes match {
        case Nil => sizesLoop(List(config.minSize), 1, rnd)
        case szs if count < 10 =>
          val (nextSize, nextRandomizer) = rnd.chooseInt(config.minSize, maxSize)
          sizesLoop(nextSize :: sizes, count + 1,  nextRandomizer)
        case _ => sizes.sorted
      }
    }
    val initialSizes = sizesLoop(Nil, 0, Randomizer.default)
    loop(0, 0, Randomizer.default, initialSizes)
  }

  def checkFor4[A, B, C, D, ASSERTION](names: List[String], config: PropertyCheckConfiguration)(fun: (A, B, C, D) => ASSERTION)
                                            (implicit
                                             genA: org.scalatest.prop.Generator[A],
                                             genB: org.scalatest.prop.Generator[B],
                                             genC: org.scalatest.prop.Generator[C],
                                             genD: org.scalatest.prop.Generator[D]
                                            ): PropertyFun.Result = {
    val maxDiscarded = PropertyCheckConfiguration.calculateMaxDiscarded(config.maxDiscardedFactor, config.minSuccessful)
    val maxSize = config.minSize + config.sizeRange

    @tailrec
    def loop(succeededCount: Int, discardedCount: Int, nextRandomizer: Randomizer, initialSizes: List[Int]): PropertyFun.Result = {
      val (size, nextInitialSizes, nextRandomizer2) =
        initialSizes match {
          case head :: tail => (head, tail, nextRandomizer)
          case Nil =>
            val (sz, r2) = nextRandomizer.chooseInt(config.minSize, maxSize)
            (sz, Nil, r2)
        }
      val (a, ar) = genA.next(10, nextRandomizer)
      val (b, br) = genB.next(10, ar)
      val (c, cr) = genC.next(10, br)
      val (d, dr) = genD.next(10, cr)
      val result: Try[Unit] = Try { fun(a, b, c, d) }
      val argsPassed = List(a, b, c, d)
      result match {
        case Success(()) =>
          val nextSucceededCount = succeededCount + 1
          if (nextSucceededCount < config.minSuccessful)
            loop(nextSucceededCount, discardedCount, dr, nextInitialSizes)
          else
            PropertyFun.Success
        case Failure(ex: DiscardedEvaluationException) =>
          val nextDiscardedCount = discardedCount + 1
          if (nextDiscardedCount < maxDiscarded)
            loop(succeededCount, nextDiscardedCount, dr, nextInitialSizes)
          else
            new PropertyFun.Exhausted(succeededCount, nextDiscardedCount)
        case Failure(ex) =>
          new PropertyFun.Failure(succeededCount, ex, names, argsPassed)
      }
    }

    @tailrec
    def sizesLoop(sizes: List[Int], count: Int, rnd: Randomizer): List[Int] = {
      sizes match {
        case Nil => sizesLoop(List(config.minSize), 1, rnd)
        case szs if count < 10 =>
          val (nextSize, nextRandomizer) = rnd.chooseInt(config.minSize, maxSize)
          sizesLoop(nextSize :: sizes, count + 1,  nextRandomizer)
        case _ => sizes.sorted
      }
    }
    val initialSizes = sizesLoop(Nil, 0, Randomizer.default)
    loop(0, 0, Randomizer.default, initialSizes)
  }

  def checkFor5[A, B, C, D, E, ASSERTION](names: List[String], config: PropertyCheckConfiguration)(fun: (A, B, C, D, E) => ASSERTION)
                                               (implicit
                                                genA: org.scalatest.prop.Generator[A],
                                                genB: org.scalatest.prop.Generator[B],
                                                genC: org.scalatest.prop.Generator[C],
                                                genD: org.scalatest.prop.Generator[D],
                                                genE: org.scalatest.prop.Generator[E]
                                               ): PropertyFun.Result = {
    val maxDiscarded = PropertyCheckConfiguration.calculateMaxDiscarded(config.maxDiscardedFactor, config.minSuccessful)
    val maxSize = config.minSize + config.sizeRange

    @tailrec
    def loop(succeededCount: Int, discardedCount: Int, nextRandomizer: Randomizer, initialSizes: List[Int]): PropertyFun.Result = {
      val (size, nextInitialSizes, nextRandomizer2) =
        initialSizes match {
          case head :: tail => (head, tail, nextRandomizer)
          case Nil =>
            val (sz, r2) = nextRandomizer.chooseInt(config.minSize, maxSize)
            (sz, Nil, r2)
        }
      val (a, ar) = genA.next(10, nextRandomizer)
      val (b, br) = genB.next(10, ar)
      val (c, cr) = genC.next(10, br)
      val (d, dr) = genD.next(10, cr)
      val (e, er) = genE.next(10, dr)
      val result: Try[Unit] = Try { fun(a, b, c, d, e) }
      val argsPassed = List(a, b, c, d, e)
      result match {
        case Success(()) =>
          val nextSucceededCount = succeededCount + 1
          if (nextSucceededCount < config.minSuccessful)
            loop(nextSucceededCount, discardedCount, er, nextInitialSizes)
          else
            PropertyFun.Success
        case Failure(ex: DiscardedEvaluationException) =>
          val nextDiscardedCount = discardedCount + 1
          if (nextDiscardedCount < maxDiscarded)
            loop(succeededCount, nextDiscardedCount, er, nextInitialSizes)
          else
            new PropertyFun.Exhausted(succeededCount, nextDiscardedCount)
        case Failure(ex) =>
          new PropertyFun.Failure(succeededCount, ex, names, argsPassed)
      }
    }

    @tailrec
    def sizesLoop(sizes: List[Int], count: Int, rnd: Randomizer): List[Int] = {
      sizes match {
        case Nil => sizesLoop(List(config.minSize), 1, rnd)
        case szs if count < 10 =>
          val (nextSize, nextRandomizer) = rnd.chooseInt(config.minSize, maxSize)
          sizesLoop(nextSize :: sizes, count + 1,  nextRandomizer)
        case _ => sizes.sorted
      }
    }
    val initialSizes = sizesLoop(Nil, 0, Randomizer.default)
    loop(0, 0, Randomizer.default, initialSizes)
  }

  def checkFor6[A, B, C, D, E, F, ASSERTION](names: List[String], config: PropertyCheckConfiguration)(fun: (A, B, C, D, E, F) => ASSERTION)
                                                  (implicit
                                                   genA: org.scalatest.prop.Generator[A],
                                                   genB: org.scalatest.prop.Generator[B],
                                                   genC: org.scalatest.prop.Generator[C],
                                                   genD: org.scalatest.prop.Generator[D],
                                                   genE: org.scalatest.prop.Generator[E],
                                                   genF: org.scalatest.prop.Generator[F]
                                                  ): PropertyFun.Result = {
    val maxDiscarded = PropertyCheckConfiguration.calculateMaxDiscarded(config.maxDiscardedFactor, config.minSuccessful)
    val maxSize = config.minSize + config.sizeRange

    @tailrec
    def loop(succeededCount: Int, discardedCount: Int, nextRandomizer: Randomizer, initialSizes: List[Int]): PropertyFun.Result = {
      val (size, nextInitialSizes, nextRandomizer2) =
        initialSizes match {
          case head :: tail => (head, tail, nextRandomizer)
          case Nil =>
            val (sz, r2) = nextRandomizer.chooseInt(config.minSize, maxSize)
            (sz, Nil, r2)
        }
      val (a, ar) = genA.next(10, nextRandomizer)
      val (b, br) = genB.next(10, ar)
      val (c, cr) = genC.next(10, br)
      val (d, dr) = genD.next(10, cr)
      val (e, er) = genE.next(10, dr)
      val (f, fr) = genF.next(10, er)
      val result: Try[Unit] = Try { fun(a, b, c, d, e, f) }
      val argsPassed = List(a, b, c, d, e, f)
      result match {
        case Success(()) =>
          val nextSucceededCount = succeededCount + 1
          if (nextSucceededCount < config.minSuccessful)
            loop(nextSucceededCount, discardedCount, fr, nextInitialSizes)
          else
            PropertyFun.Success
        case Failure(ex: DiscardedEvaluationException) =>
          val nextDiscardedCount = discardedCount + 1
          if (nextDiscardedCount < maxDiscarded)
            loop(succeededCount, nextDiscardedCount, fr, nextInitialSizes)
          else
            new PropertyFun.Exhausted(succeededCount, nextDiscardedCount)
        case Failure(ex) =>
          new PropertyFun.Failure(succeededCount, ex, names, argsPassed)
      }
    }

    @tailrec
    def sizesLoop(sizes: List[Int], count: Int, rnd: Randomizer): List[Int] = {
      sizes match {
        case Nil => sizesLoop(List(config.minSize), 1, rnd)
        case szs if count < 10 =>
          val (nextSize, nextRandomizer) = rnd.chooseInt(config.minSize, maxSize)
          sizesLoop(nextSize :: sizes, count + 1,  nextRandomizer)
        case _ => sizes.sorted
      }
    }
    val initialSizes = sizesLoop(Nil, 0, Randomizer.default)
    loop(0, 0, Randomizer.default, initialSizes)
  }

}

object PropertyFun {

  sealed trait Result

  object Success extends Result

  case class Exhausted(succeeded: Long, discarded: Long) extends Result

  case class Failure[T](succeeded: Long, ex: Throwable, names: List[String], argsPassed: List[T]) extends Result

}