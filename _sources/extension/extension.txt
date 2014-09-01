
Babel Extension
===============

This page will explains how you can extends the DSL from Babel Camel by adding new keywords and grammar parts to the existing DSL.

.. warning::
  The extension of the DSL requires some knowledge of the Scala language.
  
Existing Extensions
+++++++++++++++++++

.. toctree::
    :maxdepth: 1

    mock

Create an extension
+++++++++++++++++++

To create an extension, you need to create a new DSL, some definition objects and a Parser.

Explanation
~~~~~~~~~~~

#. Choose a package for your the new extension like ``io.xtech.babel.camel.mock``
#. Create some definition objects that extends ``io.xtech.babel.fish.parsing.StepDefinition``.
#. Create a DSL with your new keywords using the definition objects.
   The DSL will take a ``io.xtech.babel.fish.BaseDSL`` and extends a ``io.xtech.babel.fish.DSL2BaseDSL``
#. Create a trait that extends ``io.xtech.babel.camel.parsing.CamelParsing``.

   a) Declare your parser by defining a parse method which returns a ``Process`` type and add it to the ``steps`` of the trait.
   b) Declare an implicit method using your new DSL.

Example
~~~~~~~
.. includecode:: ../../../babel-camel/babel-camel-mock/src/main/scala/io/xtech/babel/camel/mock/MockDSL.scala#doc:babel-mock

