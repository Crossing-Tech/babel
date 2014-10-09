
Babel Structure
===============

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


