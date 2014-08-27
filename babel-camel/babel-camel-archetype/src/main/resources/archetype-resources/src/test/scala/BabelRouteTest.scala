package ${package}

import org.apache.camel.builder.AdviceWithRouteBuilder
import org.apache.camel.model.ModelCamelContext
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.After
import org.springframework.context.support.ClassPathXmlApplicationContext

import io.xtech.babel.camel.mock._

class BabelRouteTest extends SpecificationWithJUnit {


  "Babel" should {

    "create a simple route" in new testCase {

      applicationContext = new ClassPathXmlApplicationContext("classpath:test-context.xml")

      lazy val camelContext = applicationContext.getBean(classOf[ModelCamelContext])

      applicationContext.start()

      camelContext.getRouteDefinition("route1").adviceWith(camelContext, new AdviceWithRouteBuilder {
        def configure() {
          mockEndpointsAndSkip("direct:output")
        }
      })

      val mockEndpoint = camelContext.getMockEndpoint("direct:output")

      mockEndpoint.expectedBodiesReceived("hello toto")

      val producerTemplate = camelContext.createProducerTemplate()

      producerTemplate.sendBody("direct:input", "toto")

      mockEndpoint.assertIsSatisfied()
    }

  }

}

class testCase extends After {

  var applicationContext: ClassPathXmlApplicationContext = _

  def after {
    applicationContext.close()
  }
}