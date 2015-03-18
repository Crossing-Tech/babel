/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */
package io.xtech.babel.camel

import io.xtech.babel.camel.MarshallerSpec.{ camelCsv, camelJsonXml }
import io.xtech.babel.camel.model.CamelMessagePredicate
import io.xtech.babel.camel.test.camel
import java.util.{ ArrayList => JArrayList, HashMap => JHashMap }
import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.dataformat.csv.CsvDataFormat
import org.apache.camel.dataformat.xmljson.XmlJsonDataFormat
import org.apache.camel.impl.SimpleRegistry
import org.apache.commons.csv.writer.{ CSVConfig, CSVField }
import org.specs2.mutable.SpecificationWithJUnit

object MarshallerSpec {

  class camelJsonXml extends camel {

    lazy val jsonXmlDataFormat = {
      val dataFormat = new XmlJsonDataFormat()
      dataFormat
    }
  }

  class camelCsv extends camel {
    lazy val csvDataFormat = {
      val csvDataFormat = new CsvDataFormat

      val csvConfig = new CSVConfig()
      csvConfig.addField(new CSVField("a"))
      csvConfig.addField(new CSVField("b"))
      csvConfig.addField(new CSVField("c"))
      csvConfig.addField(new CSVField("d"))
      csvConfig.addField(new CSVField("e"))

      csvDataFormat.setConfig(csvConfig)

      csvDataFormat
    }

    lazy val inputData = {
      val expectedValue = new JArrayList[JHashMap[String, String]]
      val row = new JHashMap[String, String]
      row.put("a", "1")
      row.put("b", "2")
      row.put("c", "3")
      row.put("d", "4")
      row.put("e", "5")
      expectedValue.add(row)
      expectedValue
    }

    lazy val outputData = {
      val expectedValue = new JArrayList[JArrayList[String]]
      val row = new JArrayList[String]
      row.add("1")
      row.add("2")
      row.add("3")
      row.add("4")
      row.add("5")
      expectedValue.add(row)
      expectedValue
    }

    val csvString = "1,2,3,4,5\n"
  }

}

class MarshallerSpec extends SpecificationWithJUnit {
  sequential

  "A CSV marshaller" should {

    "unmarshal a string to an ArrayList with a reference" in new camelCsv {

      import io.xtech.babel.camel.builder.RouteBuilder

      val routeDef = new RouteBuilder {
        from("direct:input").unmarshal("csvMarshaller").to("mock:output")
      }
      routeDef.addRoutesToCamelContext(camelContext)

      val registry = new SimpleRegistry
      registry.put("csvMarshaller", csvDataFormat)

      camelContext.setRegistry(registry)

      camelContext.start()

      val mockEndpoint = camelContext.getEndpoint("mock:output").asInstanceOf[MockEndpoint]

      mockEndpoint.expectedBodyReceived().body().isEqualTo(outputData)

      val producer = camelContext.createProducerTemplate()
      producer.sendBody("direct:input", csvString)

      mockEndpoint.assertIsSatisfied()
    }

    "marshal an ArrayList to a csv string with a reference" in new camelCsv {
      //#doc:babel-camel-marshaller-1

      import io.xtech.babel.camel.builder.RouteBuilder

      val routeDef = new RouteBuilder {
        from("direct:input").
          //Message body type is transformed using a bean id defined lower
          marshal("csvMarshaller").
          to("mock:output")
      }

      val registry = new SimpleRegistry
      //csvDataFormat is a org.apache.camel.spi.DataFormat instance
      registry.put("csvMarshaller", csvDataFormat)
      //#doc:babel-camel-marshaller-1
      routeDef.addRoutesToCamelContext(camelContext)

      camelContext.setRegistry(registry)

      camelContext.start()

      val mockEndpoint = camelContext.getEndpoint("mock:output").asInstanceOf[MockEndpoint]

      mockEndpoint.expectedBodiesReceived(csvString)

      val producer = camelContext.createProducerTemplate()
      producer.sendBody("direct:input", inputData)

      mockEndpoint.assertIsSatisfied()
    }

    "marshal a HashMap to a csv string with an instance" in new camelCsv {
      //#doc:babel-camel-marshaller-2
      import io.xtech.babel.camel.builder.RouteBuilder

      val routeDef = new RouteBuilder {
        from("direct:input").
          //csvDataFormat is a org.apache.camel.spi.DataFormat instance
          marshal(csvDataFormat).
          to("mock:output")
      }
      //#doc:babel-camel-marshaller-2
      routeDef.addRoutesToCamelContext(camelContext)

      camelContext.start()

      val mockEndpoint = camelContext.getEndpoint("mock:output").asInstanceOf[MockEndpoint]

      mockEndpoint.expectedBodiesReceived(csvString)

      val producer = camelContext.createProducerTemplate()
      producer.sendBody("direct:input", inputData)

      mockEndpoint.assertIsSatisfied()
    }

    "unmarshal a csv string to an ArrayList with an instance" in new camelCsv {

      import io.xtech.babel.camel.builder.RouteBuilder

      val routeDef = new RouteBuilder {
        from("direct:input").unmarshal(csvDataFormat).to("mock:output")
      }
      routeDef.addRoutesToCamelContext(camelContext)

      camelContext.start()

      val mockEndpoint = camelContext.getEndpoint("mock:output").asInstanceOf[MockEndpoint]

      mockEndpoint.expectedBodyReceived().body().isEqualTo(outputData)

      val producer = camelContext.createProducerTemplate()
      producer.sendBody("direct:input", csvString)

      mockEndpoint.assertIsSatisfied()
    }
  }

  "a JSON XML marshaller" should {

    "marshal a json to an xml with a reference" in new camelJsonXml {

      import io.xtech.babel.camel.builder.RouteBuilder

      val routeDef = new RouteBuilder {
        from("direct:input").marshal("myMarshaller").to("mock:output")
      }
      routeDef.addRoutesToCamelContext(camelContext)

      val registry = new SimpleRegistry
      registry.put("myMarshaller", jsonXmlDataFormat)
      camelContext.setRegistry(registry)

      camelContext.start()

      val mockEndpoint = camelContext.getEndpoint("mock:output").asInstanceOf[MockEndpoint]

      mockEndpoint.expectedBodiesReceived("""{"a":"1","b":{"@c":"2"},"d":{"e":"3","f":"4"}}""")

      val producer = camelContext.createProducerTemplate()
      producer.sendBody("direct:input",
        """
          |<root>
          |  <a>1</a>
          |  <b c="2"/>
          |  <d>
          |    <e>3</e>
          |    <f>4</f>
          |  </d>
          |</root>
        """.stripMargin)

      mockEndpoint.assertIsSatisfied()
    }

    "unmarshal a json to an xml with a reference" in new camelJsonXml {

      import io.xtech.babel.camel.builder.RouteBuilder

      val routeDef = new RouteBuilder {
        from("direct:input").unmarshal("myMarshaller").to("mock:output")
      }
      routeDef.addRoutesToCamelContext(camelContext)

      val registry = new SimpleRegistry
      registry.put("myMarshaller", jsonXmlDataFormat)
      camelContext.setRegistry(registry)

      camelContext.start()

      val mockEndpoint = camelContext.getEndpoint("mock:output").asInstanceOf[MockEndpoint]

      val expectedXml = """<?xml version="1.0" encoding="UTF-8"?><o><root><a>1</a><b c="2"/><d><e>3</e><f>4</f></d></root></o>"""

      mockEndpoint.expectedMessagesMatches(new CamelMessagePredicate[String](msg => {
        val body = msg.body.getOrElse("").replace("\r\n", "")
        body == expectedXml
      }))

      val producer = camelContext.createProducerTemplate()
      producer.sendBody("direct:input",
        """{"root":{"a":"1","b":{"@c":"2"},"d":{"e":"3","f":"4"}}}""")

      mockEndpoint.assertIsSatisfied()
    }
  }
}
