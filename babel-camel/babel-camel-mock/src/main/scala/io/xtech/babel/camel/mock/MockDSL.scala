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

//#doc:babel-mock

package io.xtech.babel.camel.mock

import io.xtech.babel.camel.parsing.CamelParsing
import io.xtech.babel.fish.{DSL2BaseDSL, BaseDSL}
import io.xtech.babel.fish.model.StepDefinition
import io.xtech.babel.fish.parsing.StepInformation

import org.apache.camel.model.ProcessorDefinition

import scala.language.implicitConversions
import scala.reflect.ClassTag


case class MockDefinition(filePath: String) extends StepDefinition

class MockDSL[I: ClassTag](protected val baseDsl: BaseDSL[I]) extends DSL2BaseDSL[I] {

  /**
   * The mock keyword. Stores received exchanges in order to assert properties in tests.
   * @param endpointUri target mock endpoint which translates to "mock:endpointUri"
   * @see  io.xtech.babel.camel.mock
   * @return the possibility to add other steps to the current DSL
   */
  def mock(endpointUri: String): BaseDSL[I] = MockDefinition(endpointUri)
}

trait Mock extends CamelParsing {

  abstract override def steps = super.steps :+ parse

  val parse: Process = {
    case StepInformation(MockDefinition(uri), camelProcessor: ProcessorDefinition[_]) =>
      camelProcessor.to(s"mock:$uri")

  }

  implicit def mockDSLExtension[I: ClassTag](baseDsl: BaseDSL[I]) = new MockDSL(baseDsl)
}

//#doc:babel-mock
