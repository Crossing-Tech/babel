package ${package}.routes

class BabelCamelRoute extends RouteBuilder {

  from("direct:input").routeId("route1").processBody("hello " + _).to("direct:output")
}

