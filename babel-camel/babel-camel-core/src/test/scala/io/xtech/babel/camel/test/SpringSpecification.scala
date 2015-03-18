/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel.test

import org.apache.camel.ProducerTemplate
import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.model.ModelCamelContext
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.{ AfterExample, BeforeExample, Fragments, Step }
import org.springframework.context.ApplicationContext
import org.springframework.context.support.AbstractApplicationContext
import scala.reflect._

trait SpringSpecification {

  type ContextType <: ApplicationContext
  def applicationContext: ContextType

  def bean[A: ClassTag]: A = applicationContext.getBean(classTag[A].runtimeClass.asInstanceOf[Class[A]])
}

trait CamelSpecification {
  self: SpringSpecification =>

  def camelContext: ModelCamelContext = applicationContext.getBean(classOf[ModelCamelContext])
  def mockEndpoint(uri: String): MockEndpoint = camelContext.getEndpoint(uri, classOf[MockEndpoint])
  def producerTemplate(): ProducerTemplate = camelContext.createProducerTemplate()
}

/**
  * Base class for Babel Camel tests. The same Application Context is used for the Specification.
  */
trait CachedBabelSpringSpecification extends SpecificationWithJUnit with SpringSpecification with CamelSpecification {

  /**
    * Type of the Application Context
    */
  type ContextType <: AbstractApplicationContext

  /**
    * The Application Context used by all the tests of this Specification.
    */
  val applicationContext: ContextType

  private[this] def startContext() {
    applicationContext.start()
  }

  private[this] def stopContext(): Unit = {
    applicationContext.close()
  }

  /** the map method allows to "post-process" the fragments after their creation */
  override def map(fs: => Fragments): Fragments = Step(startContext()) ^ fs ^ Step(stopContext())
}

/**
  * Base class for Babel Camel tests. An application context is created for each tests.
  */
trait BabelSpringSpecification extends SpecificationWithJUnit with SpringSpecification with CamelSpecification with BeforeExample with AfterExample {

  private[this] var appContext: Option[ContextType] = _

  /**
    * Type of the Application Context
    */
  type ContextType <: AbstractApplicationContext

  def applicationContext: ContextType = appContext.get

  /**
    * A factory of Application Context used by each tests.
    */
  val applicationContextFactory: () => ContextType

  def before {
    appContext = Some(applicationContextFactory())
    appContext.get.start()
  }

  def after {
    appContext.get.stop()
    appContext = None
  }
}
