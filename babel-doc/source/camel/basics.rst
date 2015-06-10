
Babel Camel Basics
==================

The Babel Camel Basics part exposes which basic statement may be defined.

Messages
++++++++

In Babel Camel, the base interface which models a message which passes through the route is called Message. A Message contains a payload called body.
From Camel point of view, a Babel Message may be understood as the in Message of an Exchange with required methods to read and write the Exchange properties.

For more details, please have a look at the Transformations part.

Basics
++++++

A simple route without changing or routing the message. The output will be the same as the input.

.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/CamelDSLSpec.scala#doc:babel-camel-basic-1

The producer may set the exchange as InOnly by setting the second argument of to, to false. This would override the default behaviour of the producer.

.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/CamelDSLSpec.scala#doc:babel-camel-basic-2

The producer may set the exchange as InOut by setting the second argument of to, to true. This would override the default behaviour of the producer.

.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/CamelDSLSpec.scala#doc:babel-camel-basic-3

Route Id
++++++++

The **routeId** will give an id to the route, then this id can be used for the monitoring or during testing.

.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/RouteIdSpec.scala#doc:babel-camel-routeId

The **routeId** can not be specified as null nor an empty string.

.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/RouteIdSpec.scala#doc:babel-camel-routeId-exception-1

.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/RouteIdSpec.scala#doc:babel-camel-routeId-exception-2

.. note::
   The **routeId** keyword is member of a set of keywords which should follow directly the **from** keyword or any keyword of this set.


As
++++

A basic example with type transformation. The keyword *as* will coerce the type of the message passing within a route to a given type.

.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/AsSpec.scala#doc:babel-camel-as


RequireAs
+++++++++

A basic example with type requirement. The *requireAs* will type the exchange body for the next keyword and will accept only a message with the given type.

.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/RequireAsSpec.scala#doc:babel-camel-requireAs

The **requiredAs** lets you ensure you will always receive the expected body type. For example, the following may not work.

.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/RequireAsSpec.scala#doc:babel-camel-requireAs-exception

.. warning:: Camel also provides tools to handle data type at runtime (which may be referred to as "runtime typing"). This may cause the regular typing to modify your data after the *requireAs* keyword depending on your ecosystem. Unfortunately, there is no way for Babel to prevent such variable behaviour.

Logging
+++++++

With a **log**, you can log a defined string (which may use Camel Simple Expression Language) and define:

* the Log level
* the Log name
* a marker for this Log event

.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/LogSpec.scala#doc:babel-camel-logging

Route configuration
+++++++++++++++++++

Callbacks may be added to a given route in order to manage its lifecycle such as :

* **onInit**
* **onStart**
* **onSuspend**
* **onResume**
* **onStop**
* **onRemove**

.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/RouteConfigurationSpec.scala#doc:babel-camel-route-conf-2

Concerning the exchange lifecycle :

* **onExchangeBegin**
* **onExchangeDone**

.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/RouteConfigurationSpec.scala#doc:babel-camel-route-conf-3

Moreover, you may prevent a route from being started automatically using the **noAutoStartup** keyword.

.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/RouteConfigurationSpec.scala#doc:babel-camel-route-conf-1



Id
++++

The **id** will set an id to the previous EIP. This may be useful for visualizing your route or for statisticts.

.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/IdSpec.scala#doc:babel-camel-id-eip

The **id** may also set the consumer id, using the **routId** keyword.

.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/IdSpec.scala#doc:babel-camel-id-from

Default ids
+++++++++++

Babel provides a way to define eip ids by default (without using **id**).

To modify this default behavior, you may create your own naming strategy in your ``RouteBuilder`` such as:

.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/IdSpec.scala#doc:babel-camel-id-default

You may also define your naming strategy depending on the pattern type:

.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/IdSpec.scala#doc:babel-camel-id-strategy
