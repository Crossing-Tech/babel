Babel Camel Routing
===================

The Routing is used to define where the messages should be routed, or not.

Multicast
+++++++++

The *multicast* keywords defines a static list of outputs where the message is sent.

.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/MulticastSpec.scala#doc:babel-camel-multicast

Recipient list
++++++++++++++

The *recipientList* is like the multicast keyword, but the list can be dynamic and calculated at runtime.

.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/RecipientListSpec.scala#doc:babel-camel-recipientList

Filter
++++++

The *filter* and *filterBody* keywords filter message with a predicate.

In this example, the predicate is a function taking a message and returning a boolean.

.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/splitfilter/SimpleSplitFilterSpec.scala#doc:babel-camel-filter

Choice
++++++

The *choice* keyword gives you a way to choose where you are sending the message.

You configures a choice with *when*, *whenBody* and *otherwise* keywords.
Each when accepts a predicate. In this example the predicates are function taking message and returning a boolean.

.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/choice/SimpleChoiceSpec.scala#doc:babel-camel-choice

Splitter
++++++++

The *split* keyword is the way to split a message in pieces, the *splitBody* does the same directly on the message body.

.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/splitfilter/SimpleSplitFilterSpec.scala#doc:babel-camel-splitter

In this example the splitting is done with a function which takes the message body and returns an Iterator.


Aggregation
+++++++++++

An aggregation is a way to combine several messages in a new message. An aggregation is declared with :

* How do you combine the messages?
* How do you group the messages?
* When the aggregation is complete?

  * When a number of message is aggregated? CompletionSize
  * After a period of time? (CompletionInterval)
  * Or a combination?

The DSL contains some default implementations we will show :

* Reduce combines messages with the same type and creates a new message with the same type.
* Fold takes a seed and combines the message with this seed and creates a new message with the type of the seed.
* CamelAggregation and CamelReferenceAggregation (from the `io.xtech.babel.camel.model` package) defines an aggregation using camel specific vocabulary.

Reduce
~~~~~~

.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/AggregateSpec.scala#doc:babel-camel-aggregate-reduce

Fold
~~~~

.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/AggregateSpec.scala#doc:babel-camel-aggregate-fold

Camel Aggregation
~~~~~~~~~~~~~~~~~

.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/AggregateSpec.scala#doc:babel-camel-aggregate-camel-1

.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/AggregateSpec.scala#doc:babel-camel-aggregate-camel-2


Wire-Tap
++++++++

The *wiretap* keyword is the way to route messages to another location while they keep beeing process by the regular flow.

.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/WireTapSpec.scala#doc:babel-camel-wiretap

Validate
++++++++

The *validate* keyword validates messages passing through a route using a function or a Camel predicate.

A message will be valid only if the expression or function is returning true. Otherwise, an exception is thrown.

Camel Predicate
~~~~~~~~~~~~~~~
.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/ValidationSpec.scala#doc:babel-camel-validate-1

Message Function
~~~~~~~~~~~~~~~~
.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/ValidationSpec.scala#doc:babel-camel-validate-2

Body Function
~~~~~~~~~~~~~
.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/ValidationSpec.scala#doc:babel-camel-validate-3
