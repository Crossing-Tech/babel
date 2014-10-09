
Babel Architecture
==================

Babel overview
++++++++++++++

Babel is a Domain Specific Language (DSL). This means Babel helps you to specify what you want to achieve and not to make it directly.

You may see Babel as one more DSL for Apache Camel, among well-known ones such as the Java, Scala and XML DSL:

.. figure:: ../images/architecture-camel.png
   :align: center
   :scale: 75%

   Babel Camel is another way to write Apache Camel routes.

Babel Modules
+++++++++++++

Babel is splitted into modules:

* **babel-fish** is the core of Babel
* **babel-camel** is a connector for Apache Camel
* **babel-spring** is an experimental connector for Spring Integration

.. figure:: ../images/architecture.png
   :align: center
   :scale: 75%

   Babel's modules : **babel-fish**, **babel-camel** and the experimental **babel-spring**


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


