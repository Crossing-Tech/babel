package io.xtech.babel.camel.choice;

import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.language.ConstantExpression;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 * Created by babel on 9/26/14.
 */

public class DemoTest extends CamelTestSupport {

  @EndpointInject(uri = "mock:error")
  protected MockEndpoint errorEndpoint;

  @EndpointInject(uri = "mock:success")
  protected MockEndpoint successEndpoint;

  @Produce(uri = "direct:start")
  protected ProducerTemplate template;

  @Test
  public void testSendMatchingMessage() throws Exception {

    successEndpoint.expectedBodiesReceived(6);
    errorEndpoint.expectedBodiesReceived("-1 is negative");

    template.sendBody("1,2,3");

    successEndpoint.assertIsSatisfied();

    template.sendBody("1,2,-4");

    errorEndpoint.assertIsSatisfied();

  }

  //#doc:babel-camel-demo-java-2
  Expression correlation =
      new ConstantExpression("a");
  AggregationStrategy strategy =
      new AggregationStrategy() {
    @Override
    public Exchange aggregate
        (Exchange old, Exchange news) {
      if (news == null) return old;
      else if (old == null) return news;
      else {
        int result = old.getIn().
            getBody(Integer.class)
            + news.getIn().getBody(Integer.class);
        old.getIn().setBody(result);
        return old;
      }
    }
  };


  //#doc:babel-camel-demo-java-2


  @Override
  protected RouteBuilder createRouteBuilder() {
    //#doc:babel-camel-demo-java-1
    class MyRoute extends RouteBuilder {
      public void configure() {
        from("direct:start").
            split(body(String.class).
                tokenize(",")).

            aggregate(correlation, strategy).
            completionSize(3).
            choice().
            when(body(Integer.class).
                isGreaterThan(0)).
            to("mock:success").

            when(body(Integer.class).
                isLessThan(0)).
            process(errorProcessor).
            to("mock:error").
            end();
      }
    }

    //#doc:babel-camel-demo-java-1
    return new MyRoute();
  }

  //#doc:babel-camel-demo-java-3
  Processor errorProcessor = new Processor() {
    @Override
    public void process(Exchange exchange)
        throws Exception {
      String errorMessage = exchange.getIn().
          getBody() + " is negative";
      exchange.getIn().setBody(errorMessage);
    }
  };
  //#doc:babel-camel-demo-java-3
}