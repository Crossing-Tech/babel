/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

//#doc:babel-mock

package io.xtech.babel.camel.mock

import io.xtech.babel.camel.parsing.CamelParsing
import io.xtech.babel.fish.model.StepDefinition
import io.xtech.babel.fish.parsing.StepInformation
import io.xtech.babel.fish.{ BaseDSL, DSL2BaseDSL }

import org.apache.camel.model.ProcessorDefinition

import scala.language.implicitConversions
import scala.reflect.ClassTag

trait Mock extends CamelParsing {

  case class MockDefinition(filePath: String) extends StepDefinition

  abstract override def steps = super.steps :+ ({
    case StepInformation(MockDefinition(uri), camelProcessor: ProcessorDefinition[_]) =>
      camelProcessor.to(s"mock:$uri")
  }: Process)

  implicit def mockDSLExtension[I: ClassTag](dsl: BaseDSL[I]) = new DSL2BaseDSL[I] {

    protected val baseDsl = dsl

    /**
      * The mock keyword. Stores received exchanges in order to assert properties in tests.
      * @param endpointUri target mock endpoint which translates to "mock:endpointUri"
      * @see  io.xtech.babel.camel.mock
      * @return the possibility to add other steps to the current DSL
      */
    def mock(endpointUri: String): BaseDSL[I] = MockDefinition(endpointUri)
  }

}

//#doc:babel-mock
