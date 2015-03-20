/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel

import io.xtech.babel.camel.model.CamelMessage
import io.xtech.babel.camel.test.camel
import org.apache.camel.ExchangePattern
import org.specs2.mutable.SpecificationWithJUnit

class CamelMessageSpec extends SpecificationWithJUnit {
  sequential

  "A CamelMessage" should {

    val headerA = "a"
    val headerB = "b"

    "not be created with a null camel message" in new camel {

      val message = new CamelMessage(null) must throwA[IllegalArgumentException]
    }

    "be created with nothing" in new camel {
      val camelExchange = createExchange()
      val camelMessage = camelExchange.getIn()

      val message = new CamelMessage(camelMessage)
      message.headers.size mustEqual 0
      message.body must beNone
    }

    "be created with a string" in new camel {
      val camelExchange = createExchange()
      val camelMessage = camelExchange.getIn()
      camelMessage.setBody("bla")

      val message = new CamelMessage[String](camelMessage)
      message.body must beSome("bla")
    }

    "get an converted body" in new camel {
      val camelExchange = createExchange()
      val camelMessage = camelExchange.getIn()
      camelMessage.setBody("42")

      val message = new CamelMessage[String](camelMessage)
      message.bodyAs[Int] must beSome(42)
    }

    "update a body" in new camel {
      val camelExchange = createExchange()
      val camelMessage = camelExchange.getIn()
      camelMessage.setBody("bla")

      val message = new CamelMessage[String](camelMessage)
      val newMsg = message.withBody(_ + "bli")
      newMsg.body must beSome("blabli")
    }

    "update an optional empty body" in new camel {
      val camelExchange = createExchange()
      val camelMessage = camelExchange.getIn()
      camelMessage.setBody("bla")

      val message = new CamelMessage[String](camelMessage)
      val newMsg = message.withOptionalBody(_.map(_ + "bli"))
      newMsg.body must beSome("blabli")
    }

    "update an empty body" in new camel {
      val camelExchange = createExchange()
      val camelMessage = camelExchange.getIn()

      val message = new CamelMessage[String](camelMessage)
      val newMsg = message.withOptionalBody(_.map(_ + "bli"))
      newMsg.body must beNone
    }

    "return the headers" in new camel {
      val camelExchange = createExchange()
      val camelMessage = camelExchange.getIn()
      camelMessage.setHeader(headerA, 1)
      camelMessage.setHeader(headerB, "2")

      val message = new CamelMessage[String](camelMessage)
      message.headers must havePairs(headerA -> 1, headerB -> "2")
    }

    "update a header" in new camel {
      val camelExchange = createExchange()
      val camelMessage = camelExchange.getIn()
      camelMessage.setHeader(headerA, 1)
      camelMessage.setHeader(headerB, "2")

      val message = new CamelMessage[String](camelMessage)
      val newMsg = message.withHeader("c", 42)
      message.headers must havePairs(headerA -> 1, headerB -> "2", "c" -> 42)
    }

    "update headers" in new camel {
      val camelExchange = createExchange()
      val camelMessage = camelExchange.getIn()
      camelMessage.setHeader(headerA, 1)
      camelMessage.setHeader(headerB, "2")

      val message = new CamelMessage[String](camelMessage)
      val newMsg = message.withHeaders(headers => headers ++ Map("c" -> 42, "d" -> "dd"))
      message.headers must havePairs(headerA -> 1, headerB -> "2", "c" -> 42, "d" -> "dd")

    }

    "remove headers" in new camel {
      val camelExchange = createExchange()
      val camelMessage = camelExchange.getIn()
      camelMessage.setHeader(headerA, "to be removed")

      val message = new CamelMessage[String](camelMessage)
      val newMsg = message.withHeaders(headers => Map("c" -> 42))
      message.headers.size === 1
      message.headers must havePairs("c" -> 42)

    }

    "return the properties" in new camel {
      val camelExchange = createExchange()
      val camelMessage = camelExchange.getIn()
      camelMessage.getExchange.setProperty(headerA, 1)
      camelMessage.getExchange.setProperty(headerB, "2")

      val message = new CamelMessage[String](camelMessage)
      message.exchangeProperties must havePairs(headerA -> 1, headerB -> "2")
    }

    "update a header" in new camel {
      val camelExchange = createExchange()
      val camelMessage = camelExchange.getIn()
      camelMessage.getExchange.setProperty(headerA, 1)
      camelMessage.getExchange.setProperty(headerB, "2")

      val message = new CamelMessage[String](camelMessage)
      val newMsg = message.withExchangeProperty("c", 42)
      message.exchangeProperties must havePairs(headerA -> 1, headerB -> "2", "c" -> 42)
    }

    "update properties" in new camel {
      val camelExchange = createExchange()
      val camelMessage = camelExchange.getIn()
      camelMessage.getExchange.setProperty(headerA, 1)
      camelMessage.getExchange.setProperty(headerB, "2")

      val message = new CamelMessage[String](camelMessage)
      val newMsg = message.withExchangeProperties(properties => properties ++ Map("c" -> 42, "d" -> "dd"))
      message.exchangeProperties must havePairs(headerA -> 1, headerB -> "2", "c" -> 42, "d" -> "dd")

    }

    "remove properties" in new camel {
      val camelExchange = createExchange()
      val camelMessage = camelExchange.getIn()
      camelMessage.getExchange.setProperty(headerA, "to be removed")

      val message = new CamelMessage[String](camelMessage)
      val newMsg = message.withExchangeProperties(properties => Map("c" -> 42))
      newMsg.exchangeProperties.size === 1
      newMsg.exchangeProperties must havePairs("c" -> 42)

    }

    "return the exception" in new camel {
      val camelExchange = createExchange()
      camelExchange.setException(new IllegalArgumentException("bla"))
      val camelMessage = camelExchange.getIn()

      val message = new CamelMessage[String](camelMessage)
      message.exchangeException.getMessage must be_==("bla")
    }

    "return the exchange pattern" in new camel {
      val camelExchange = createExchange()
      camelExchange.setPattern(ExchangePattern.InOptionalOut)
      val camelMessage = camelExchange.getIn()

      val message = new CamelMessage[String](camelMessage)
      message.exchangePattern must be_==(ExchangePattern.InOptionalOut)
    }

    "set the exchange pattern" in new camel {
      val camelExchange = createExchange()
      camelExchange.setPattern(ExchangePattern.InOnly)
      val camelMessage = camelExchange.getIn()

      val message = new CamelMessage[String](camelMessage)
      message.withExchangePattern(ExchangePattern.InOptionalOut).exchangePattern must be_==(ExchangePattern.InOptionalOut)
    }
  }
}
