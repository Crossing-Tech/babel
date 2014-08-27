Introduction
------------

Babel is an efficient way to write your integration solution. It stands as a generic Domain Specific Language (DSL) specially made for integration duties.

Implementation of Babel have been done for the following frameworks, which allows you to use them with the improvements provided by Babel syntax:

* `Apache Camel <http://camel.apache.org/>`_

You may find experimental integration of the following frameworks in the Babel Experimental project which is not covered here:

* `Spring Framework Integration <http://projects.spring.io/spring-integration/>`_

To use Babel on top of Camel, you may use :ref:`babel-camel-guide`, which builds your integration code to be run by the Camel runtime and allows you to use some interfaces that are specially made to integrate well with Camel. Please have a look to the Babel Camel documentation for more details and examples.

.. includecode:: ../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/CamelDSLTest.scala#doc:babel-camel-example
