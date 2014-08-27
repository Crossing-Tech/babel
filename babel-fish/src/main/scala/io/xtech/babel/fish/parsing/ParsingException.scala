/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.fish.parsing

object ParsingException {

  /**
    * An exception thrown when an unknown step (definition) is found in the Babel fish DSL.
    * @param step the unknown step
    */
  class UnknownStepException(step: String) extends Exception(s"The step '$step' has not been successfully parsed.")

}
