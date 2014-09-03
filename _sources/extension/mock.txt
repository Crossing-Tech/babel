
Mock Extension
===============

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

.. includecode:: ../../../babel-camel/babel-camel-mock/src/test/scala/io/xtech/babel/camel/MockSpec.scala#doc:babel-camel-mock

