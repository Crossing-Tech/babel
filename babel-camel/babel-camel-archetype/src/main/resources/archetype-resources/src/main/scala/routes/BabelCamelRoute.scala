package ${package}.routes

import io.xtech.babel.camel.builder.RouteBuilder

class BabelCamelRoute extends RouteBuilder {

  from("direct:input").routeId("route1").processBody("hello " + _).to("direct:output")
}

