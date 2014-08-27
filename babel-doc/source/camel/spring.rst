
Babel Camel using the Spring Application Context
================================================

This page explains how to integrate a Babel Camel route with a Spring Application Context.

A route defined with the Babel Camel DSL may be transparently loaded into a Spring Application Context, such as that:

.. highlight:: xml
.. includecode:: ../../../babel-camel/babel-camel-core/src/test/resources/META-INF/spring/context.xml
.. highlight:: scala

With the corresponding Babel Camel route:

.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/builder/spring/MyRouteBuilder.scala#doc:babel-camel-spring

.. warning:: Unfortunately, the injection using setters may cause Babel initialization to fail because Babel may get intialized before every required Spring Bean. To handle this, please use the ``io.xtech.cf.babel.camel.builder.SpringRouteBuilder`` and define your route in the *configure* method body.
   You may also use the constructor injection if you prefer to rely on the basic ``io.xtech.babel.camel.builder.RouteBuilder``.
.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/builder/springinjection/SetterInjectionRouteBuilder.scala#doc:babel-camel-spring-setter