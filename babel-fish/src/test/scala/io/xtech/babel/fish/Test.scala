/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.fish

import io.xtech.babel.fish.model.{ Sink, Source }
import scala.collection.immutable
import scala.language.implicitConversions

/**
  * This class defines what is a source
  * @param uri a uri
  * @tparam O The type of Object the source generates.
  */
class TestSource[+O](val uri: String) extends Source[O]

/**
  * This class defines what is a sink
  * @param uri a uri
  * @tparam I The type of Object the sink accepts.
  * @tparam O The type of Object the sink generates.
  */
class TestSink[-I, +O](val uri: String) extends Sink[I, O]

/**
  * It defines the implicits that make the fish works.
  */
object Test {

  implicit def stringSource(uri: String) = new TestSource(uri)

  implicit def seqStringSink(uris: Seq[String]) = immutable.Seq(uris.map(new TestSink(_)): _*)

  implicit def stringSink(uri: String) = new TestSink(uri)

  implicit def testSource(source: Source[_]): TestSource[_] = {

    source match {
      case testSource: TestSource[_] => {
        testSource
      }
    }
  }

  implicit def testSink(sink: Sink[_, _]): TestSink[_, _] = {

    sink match {
      case testSink: TestSink[_, _] => {
        testSink
      }
    }
  }
}
