
Babel Architecture
==================

Babel Modules
+++++++++++++

You may see Babel as one more DSL for Apache Camel, among well-known ones such as the Java, Scala and XML DSL:

.. figure:: ../images/architecture-camel.png
   :align: center
   :scale: 75%

   Babel Camel is another way to write Apache Camel routes.


This Babel Camel DSL is provided through the **babel-camel** module which may be seen as the Babel connector for Apache Camel. If you want to use the Spring Framework Integration, you should use the **babel-spring** module. Finally, both are depending on the **babel-fish** which may be seen as the core of Babel.

.. figure:: ../images/architecture.png
   :align: center
   :scale: 65%

   Babel provides two main DSL to work either with Apache Camel or Spring Framework Integration, both depending on the core of Babel: **babel-fish**.

Babel Structure
+++++++++++++++

Each of the presented Babel module is composed of the following objects:

* Domain Specific Language (DSL)
* Definition Objects (model)
* Parser

DSL
~~~

- The DSL is used by the user when declaring the flow of a route.
- The DSL exposes an API and creates definition object used by the parser.

Definition Objects
~~~~~~~~~~~~~~~~~~

- Each definition object defines the behaviour of an EIP in the route.
- All the definitions define the model of the route.

Parser
~~~~~~

- A parser takes definition objects and use them to configure the runtime of the route (Apache Camel, Spring Integration).
- A route is defined with all its definition objects.


