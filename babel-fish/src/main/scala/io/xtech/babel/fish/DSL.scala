/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.fish

import io.xtech.babel.fish.model._
import scala.collection.immutable
import scala.reflect._

/**
  * defines a predicate on the message.
  */
case class MessagePredicate[-I](predicate: (Message[I] => Boolean)) extends Predicate[I]

/**
  * defines a predicate on the message body.
  */
case class BodyPredicate[-I](predicate: I => Boolean) extends Predicate[I]

/**
  * defines an expression on the message
  */
case class MessageExpression[-I, +O](expression: (Message[I] => O)) extends Expression[I, O]

/**
  * defines a transformation on a message as an expression.
  * This means the transformation may modify the headers its output.
  */
case class MessageTransformationExpression[-I, +O](expression: (Message[I] => Message[O])) extends Expression[I, O]

/**
  * defines an expression on the message body.
  */
case class BodyExpression[-I, +O](expression: (I => O)) extends Expression[I, O]

/**
  * A specific DSL for a choice block.
  * @param choice the definition
  * @tparam I the body type of the input message.
  */
class ChoiceDSL[I: ClassTag](choice: ChoiceDefinition[I]) extends BaseDSL[I](choice) {

  /**
    * declares a possible subroute in the routing.
    * @param predicate a function taking a message returning a boolean. if true the message go through the subroute.
    * @return the possibility to add other steps to the current DSL
    */
  def when(predicate: (Message[I] => Boolean)): BaseDSL[I] = {

    val whenDef = new WhenDefinition(MessagePredicate(predicate))

    choice.scopedSteps = choice.scopedSteps :+ whenDef

    new BaseDSL[I](whenDef)
  }

  /**
    * declares a possible subroute in the routing.
    * @param predicate if true the message go through the subroute.
    * @return the possibility to add other steps to the current DSL
    */
  def when(predicate: Predicate[I]): BaseDSL[I] = {

    val whenDef = new WhenDefinition(predicate)

    choice.scopedSteps = choice.scopedSteps :+ whenDef

    new BaseDSL[I](whenDef)
  }

  /**
    * declares a possible subroute in the routing.
    * @param predicate a function taking a message body returning a boolean. if true the message go through the subroute.
    * @return the base dsl.
    */
  def whenBody(predicate: (I => Boolean)): BaseDSL[I] = {

    val whenDef = new WhenDefinition(BodyPredicate(predicate))

    choice.scopedSteps = choice.scopedSteps :+ whenDef

    new BaseDSL[I](whenDef)
  }

  /**
    * declares a default subroute if no other subroute handle a message.
    * @return the possibility to add other steps to the current DSL
    */
  def otherwise: BaseDSL[I] = {
    val otherwiseDef = new OtherwiseDefinition

    choice.otherwise = Some(otherwiseDef)

    new BaseDSL[I](otherwiseDef)
  }
}

/**
  * A specific DSL that defines the behaviour of the route.
  * @param step the definition.
  * @tparam I the input type of this keyword.
  */
class FromDSL[I: ClassTag](step: StepDefinition) extends BaseDSL[I](step)

/**
  * Defines the empty DSL base
  */
trait NoDSL

/**
  * The base class for all DSL.
  * @param step the definition.
  * @tparam I the input type of this keyword.
  */
class BaseDSL[I: ClassTag](protected[babel] val step: StepDefinition) extends NoDSL with DSL2BaseDSL[I] {

  protected lazy val baseDsl: BaseDSL[I] = this

  /**
    * Declares an output for the route.
    * @param sink an output.
    * @param convert an object to route output (sink).
    * @tparam O the type of the output
    * @tparam S a sink object
    * @return the possibility to add other steps to the current DSL
    */
  def to[O: ClassTag, S](sink: S)(implicit convert: S => Sink[I, O]): BaseDSL[O] = {

    EndpointDefinition(sink)
  }

  /**
    * Declares an output for the route and defines incoming messages should require an answer or not.
    * @param sink an output.
    * @param requestReply is true if the sender expects an answer.
    * @param convert an object to route output (sink).
    * @tparam O the type of the output
    * @tparam S a sink object
    * @return the possibility to add other steps to the current DSL
    */
  def to[O: ClassTag, S](sink: S, requestReply: Boolean)(implicit convert: S => Sink[I, O]): BaseDSL[O] = {

    EndpointDefinition(sink, Some(requestReply))
  }

  /**
    * Declares a multicast in the route. A multicast routes the same message to multiple endpoints.
    * @param sinks the endpoints.
    * @param convert a way to convert the native type of the endpoints to sinks.
    * @tparam S the native type of the endpoints.
    * @return the possibility to add other steps to the current DSL.
    */
  def multicast[S](sinks: S*)(implicit convert: Seq[S] => immutable.Seq[Sink[I, _]]): BaseDSL[I] = {

    MulticastDefinition(sinks)
  }

  /**
    * Declares a transformation of the message body in the route with a function.
    * @param func the function
    * @tparam O the output type of the function
    * @return the possibility to add other steps to the current DSL
    */
  def processBody[O: ClassTag](func: (I => O)): BaseDSL[O] = {

    TransformerDefinition(BodyExpression(func))
  }

  /**
    * Declares a transformation of the message body in the route with a function.
    * @param func the function
    * @tparam O the output type of the function
    * @return the possibility to add other steps to the current DSL
    */
  def processBody[O: ClassTag](func: (I => O), processorId: String): BaseDSL[O] = {

    TransformerDefinition(BodyExpression(func), Some(processorId))
  }

  /**
    * Declares a transformation of the message in the route with a function.
    * @param func the function
    * @tparam O the type of the message if the body changed
    * @return the possibility to add other steps to the current DSL
    */
  def process[O: ClassTag](func: (Message[I] => Message[O])): BaseDSL[O] = {

    TransformerDefinition(MessageTransformationExpression(func))
  }

  /**
    * Declares a transformation of the message in the route with a function.
    * @param func the function
    * @tparam O the type of the message if the body changed
    * @return the possibility to add other steps to the current DSL
    */
  def process[O: ClassTag](func: (Message[I] => Message[O]), processorId: String): BaseDSL[O] = {

    TransformerDefinition(MessageTransformationExpression(func), Some(processorId))
  }

  /**
    * Declares a filter in the route. If the condition of the predicate are not meet the routing of the current
    * message is stopped.
    * @param function  taking a message and returning a boolean. (true if the message continue
    *                  otherwise false)
    * @return the possibility to add other steps to the current DSL
    */
  def filter(function: (Message[I] => Boolean)): BaseDSL[I] = {

    val predicate = MessagePredicate(function)

    FilterDefinition(predicate)

  }

  /**
    * Declares a filter in the route. If the condition of the predicate are not meet the routing of the current
    * message is stopped.
    * @param predicate  if true the message continue the route
    * @return the possibility to add other steps to the current DSL
    */
  def filter(predicate: Predicate[I]): BaseDSL[I] = {

    FilterDefinition(predicate)
  }

  /**
    * Adds a filter to the route with a predicate. If the condition of the predicate are not meet the routing of the current
    * message is stopped.
    * @param function  taking a message body and returning a boolean. (true if the message continue
    *                  otherwise false)
    * @return the main DSL
    */
  def filterBody(function: (I => Boolean)): BaseDSL[I] = {

    val predicate = BodyPredicate(function)

    FilterDefinition(predicate)

  }

  /**
    * Declares a type requirement in the route. if the body type is incorrect, the system tries to convert the message body.
    * @tparam O the required type.
    * @return the possibility to add other steps to the current DSL
    */
  def as[O: ClassTag]: BaseDSL[O] = {

    val inputClass = classTag[I].runtimeClass.asInstanceOf[Class[I]]
    val outputClass = classTag[O].runtimeClass.asInstanceOf[Class[O]]

    BodyConvertorDefinition(inputClass, outputClass)
  }

  /**
    * Declares a type requirement in the route. if the type is incorrect an exception is thrown.
    * @tparam O the required type.
    * @return the possibility to add other steps to the current DSL
    */
  def requireAs[O: ClassTag]: BaseDSL[O] = {
    val inputClass = classTag[I].runtimeClass.asInstanceOf[Class[I]]
    val outputClass = classTag[O].runtimeClass.asInstanceOf[Class[O]]

    BodyTypeValidationDefinition(inputClass, outputClass)

  }

  /**
    * Declares a static router. The routing conditions are declared inside a block using a specific DSL for the choice.
    * The choice DSL contains two keywords : when and otherwise.
    * When declares a subroute with a condition.
    * Otherwise declare a subroute for messages that don't meet the conditions of the other When subroutes.
    * {{{
    * choice{ c =>
    *     c.when(_.body == "1").endpoint("mock:output1")
    *     c.when(_.body == "2").endpoint("mock:output2")
    *     c.otherwise.endpoint("mock:output3")
    * }
    * }}}
    * @param block a function taking the routing DSL as a parameter and returning Unit.
    * @return the possibility to add other steps to the current DSL.
    */
  def choice(block: (ChoiceDSL[I] => Unit)): BaseDSL[I] = {

    val choiceDef = ChoiceDefinition[I]
    step.next = Some(choiceDef)

    val choiceDSL = new ChoiceDSL[I](choiceDef)
    block(choiceDSL)

    new BaseDSL[I](choiceDef)
  }

  /**
    * Declare a message splitter or a way to split a message body in pieces.
    * @param splitter the function used to split the body. It takes the body message and returns an Iterator of the pieces.
    * @tparam O the type of the message pieces.
    * @return the possibility to add other steps to the current DSL.
    */
  def splitBody[O: ClassTag](splitter: (I => Iterator[O])): BaseDSL[O] = {

    SplitterDefinition(BodyExpression(splitter))
  }

  /**
    * Declare a message splitter or a way to split a message body in pieces.
    * @param splitter the function used to split the message. It takes the message and returns an Iterator of the pieces.
    *
    * @tparam O the type of the message pieces.
    * @return the possibility to add other steps to the current DSL.
    */
  def split[O: ClassTag](splitter: (Message[I] => Iterator[O])): BaseDSL[O] = {

    SplitterDefinition(MessageExpression(splitter))
  }

  /**
    * Declare a message splitter or a way to split a message body in pieces.
    * @param expression  used to split the message.
    *
    * @tparam O the type of the message pieces.
    * @return the possibility to add other steps to the current DSL.
    */
  def split[O: ClassTag](expression: Expression[I, O]): BaseDSL[O] = {

    SplitterDefinition(expression)
  }

  /**
    * Declare a a way to aggregate message. The declaration is done with an object that implements the AggregateProps trait.
    * @tparam O the type of the message pieces.
    * @return the possibility to add other steps to the current DSL.
    */
  def aggregate[O: ClassTag](aggregation: AggregationConfiguration[I, O]): BaseDSL[O] = {

    AggregationDefinition(aggregation)
  }
}

class RouteDefinitionException(val errors: immutable.Seq[ValidationError]) extends Exception("The route has validation errors") {
  override def toString(): String = {
    s"${super.toString()} (${errors.map(_.errorMessage).mkString(",")})"
  }
}

/**
  * The Class to use when creating the route.
  */
class DSL() {

  private[this] var fromDefinition: immutable.Set[FromDefinition] = immutable.Set()

  /**
    * Declares the start of a route with a source.
    * @param source the source.
    * @return the possibility to add other steps to the current DSL
    */
  def from[O: ClassTag, S](source: S)(implicit convert: S => Source[O]): FromDSL[O] = {

    val outputClass = classTag[O].runtimeClass.asInstanceOf[Class[O]]
    val fromDef = FromDefinition(outputClass, source)
    fromDefinition ++= immutable.Set(fromDef)
    new FromDSL[O](fromDef)
  }

  /**
    * Builds the fish
    * @return a the definition of the dsl (containing several routes).
    * @throws RouteDefinitionException if the route defined is not valid.
    */
  protected[babel] def build(): immutable.Set[RouteDefinition] = fromDefinition match {

    // throw an exception if no from is defined
    case set if set.size == 0 =>
      throw new RouteDefinitionException(immutable.Seq(ValidationError("No routes defined in the DSL")))
    case fromDefs => fromDefs.map(fromDef => {
      // validate the route
      val validationErrors = fromDef.validate()
      validationErrors match {
        case Nil => RouteDefinition(fromDef)
        case errors: immutable.Seq[_] => {
          throw new RouteDefinitionException(errors)
        }
      }
    })
  }
}