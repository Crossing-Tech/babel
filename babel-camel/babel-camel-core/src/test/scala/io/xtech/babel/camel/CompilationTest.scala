/*
 *
 *    ___                      _   _     _ _          ___        _
 *   / __|___ _ _  _ _  ___ __| |_(_)_ _(_) |_ _  _  | __|_ _ __| |_ ___ _ _ _  _  TM
 *  | (__/ _ \ ' \| ' \/ -_) _|  _| \ V / |  _| || | | _/ _` / _|  _/ _ \ '_| || |
 *   \___\___/_||_|_||_\___\__|\__|_|\_/|_|\__|\_, | |_|\__,_\__|\__\___/_|  \_, |
 *                                             |__/                          |__/
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */
package io.xtech.babel.camel

import io.xtech.babel.fish.model.Message

import org.specs2.mutable.Specification
import io.xtech.babel.camel.model.Aggregation.{ CompletionSize, ReduceBody }

class CompilationTest extends Specification {

  "The Scala compiler" should {

    "compile this CamelDSL route" in {

      import io.xtech.babel.camel.builder.RouteBuilder

      val reduceBody = ReduceBody((a: Int, b: Int) => a + b, (msg: Message[Int]) => "a", completionStrategies = List(CompletionSize(3)))

      new RouteBuilder {
        from("direct:input").as[Int].aggregate(reduceBody).marshal("id").bean("id").to("direct:output")
      }

      success
    }
  }
}
