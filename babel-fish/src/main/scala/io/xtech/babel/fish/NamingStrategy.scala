package io.xtech.babel.fish

import io.xtech.babel.fish.model.StepDefinition

/**
  * The NamingStragegy trait lets define how pattern should be identified in the targeted framework.
  * It allows the definition of ids depending on the the squence of EIP currently parsed.
  */
trait NamingStrategy {

  /**
    * Identifies the current sequence of EIP.
    */
  protected[babel] var routeId: Option[String] = None

  /**
    * Defines how Babel tanslates an EIP to its id, if this is the case.
    * @param stepDefinition the pattern for which an id is requested.
    * @return None if Babel should not provide an id for this pattern, Some id otherwise.
    */
  def name(stepDefinition: StepDefinition): Option[String]

  /**
    * Used when parsing a new sequence of EIP.
    * Helpful if your naming index the pattern with their position in the sequence.
    */
  protected[babel] def newRoute(): Unit

}

