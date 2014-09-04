
Babel Camel Transformations
===========================

A transformation is a way to modify the message.

Marshalling
+++++++++++

A marshaller is a way to change the format of the message.
For example : XML <-> JSON

With the *marshal* and *unmarshal* keyword, you can choose the direction of the marshalling.

The keywords accepts an instance of a Camel DataFormat object or a reference to an object in a registry (ex : Spring Context).

With a Dataformat
~~~~~~~~~~~~~~~~~

.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/MarshallerSpec.scala#doc:babel-camel-marshaller-1

With a reference
~~~~~~~~~~~~~~~~

.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/MarshallerSpec.scala#doc:babel-camel-marshaller-2

Sorting
+++++++

The *sort* keyword give a way to sort a part of a message. It accepts two parameters:

* a function that split a part of the message.
* an optional comparator that give how the ordering will be done.

The result of the processing of this keyword will be of type java.util.List

.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/SortSpec.scala#doc:babel-camel-sort-1

You may also provide an `Ordering` to the *sort*:

.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/SortSpec.scala#doc:babel-camel-sort-2-1

.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/SortSpec.scala#doc:babel-camel-sort-2-2

Resequencer
+++++++++++

The *resequence* keyword is useful for sorting messages coming out of order. There are two algorithms :

Batch resequencing collects messages into a batch, sorts the messages and sends them to their output.

.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/ResequencerSpec.scala#doc:babel-camel-resequence-1

Stream resequencing re-orders (continuous) message streams based on the detection of gaps between messages.

.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/ResequencerSpec.scala#doc:babel-camel-resequence-2


Enrich
++++++

The *enrich* and pollEnrich retrieves additional data from an endpoint and let you combine the original and new message with
an aggregator. The *enrich* is using a request-reply pattern with the endpoint.

.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/EnricherSpec.scala#doc:babel-camel-enricher-1

The pollEnrich is more polling the endpoint with a timeout if no message is received after a while.

.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/EnricherSpec.scala#doc:babel-camel-enricher-2

Processors
++++++++++

You can transform a message including your own business logic. Such data transformation may be defined either by a function or using a bean. The functional way is always preferred in the Babel philosophy.

With a function
~~~~~~~~~~~~~~~

You can transform a message with a function.

The *processBody* keyword works on message bodies.

.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/CamelDSLSpec.scala#doc:babel-camel-processBody-1


The *process* keyword works on messages.

.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/CamelDSLSpec.scala#doc:babel-camel-process-1

With a Bean
~~~~~~~~~~~

You can transform a message with a bean (using camel way to handle beans)

.. warning:: This keyword will remove type safety for the rest of your route, thus it has been deprecated and might disappear if no user does require it.



With a reference in Camel registry (or in Spring Context):


.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/TransformerSpec.scala#doc:babel-camel-bean-1

.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/TransformerSpec.scala#doc:babel-camel-bean-2

With an instance:

.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/TransformerSpec.scala#doc:babel-camel-bean-3

With a class:

.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/TransformerSpec.scala#doc:babel-camel-bean-4