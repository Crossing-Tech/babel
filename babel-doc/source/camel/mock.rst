
Mock Extension
===============

Mock is the first extension of the Babel Camel DSL. It provides both a DSL and some helpers.

Description
+++++++++++

An extension for Babel Camel declaring some helpers for testing.

Requirement
~~~~~~~~~~~

The ``babel-camel-mock`` module needs to in the classpath.

Usage
+++++

Import the ``io.xtech.babel.camel.mock._`` package and extends the ``RouteBuilder`` with the ``Mock`` trait.

mock component
~~~~~~~~~~~~~~

For testing a mock endpoint can be declared with the mock endpoint.

.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/MockSpec.scala#doc:babel-camel-mock

The mock keyword keeps the type of the payload as the mock component do not modify the received messages in most of the cases. For more complexe cases, such as when using ``returnReplyBody``, you may fallback to the legacy way to define mock endpoints.

