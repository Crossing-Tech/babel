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

  def configure() {
    from("direct:babel-rb-setter").as[String].
      processBody(aBean.doSomething).
      to("mock:babel-rb-setter")
  }
}
//#doc:babel-camel-spring-setter

class ConstructorInjectionRouteBuilder @Autowired() (aBean: MyBeanProcessor) extends RouteBuilder {

  from("direct:babel-rb-setter").bean(aBean).to("mock:babel-rb-setter")
}

