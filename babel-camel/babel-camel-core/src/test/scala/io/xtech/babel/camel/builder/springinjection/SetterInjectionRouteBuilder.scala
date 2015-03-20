/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel.builder.springinjection

import io.xtech.babel.camel.builder.{ RouteBuilder, SpringRouteBuilder }
import org.springframework.stereotype.Component

@Component
class MyBeanProcessor {
  def doSomething(str: String): String = str + "bla"
}

//#doc:babel-camel-spring-setter
import org.springframework.beans.factory.annotation.Autowired
import scala.beans.BeanProperty

class SetterInjectionRouteBuilder extends SpringRouteBuilder {

  @Autowired
  @BeanProperty
  var aBean: MyBeanProcessor = _

  def configure(): Unit = {
    from("direct:babel-rb-setter").as[String].
      processBody(aBean.doSomething).
      to("mock:babel-rb-setter")
  }
}
//#doc:babel-camel-spring-setter

class ConstructorInjectionRouteBuilder @Autowired() (aBean: MyBeanProcessor) extends RouteBuilder {

  from("direct:babel-rb-setter").bean(aBean).to("mock:babel-rb-setter")
}

