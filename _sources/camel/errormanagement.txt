
Babel Camel Error management
============================

Babel provides mainly two ways to deal with errors : depending on if the handling is Exception specific or not. In any case, the error handling is done using the **handle** keyword.

.. figure:: ../grammar-diagrams/handler-2.svg
   :align: center
   :scale: 100 %
   :alt: Handle keyword

   Error handling
   **handle** can handle one or several error-handling

.. note::
   The error handling keywords, when defined in a route, are member of a set of keywords which should follow directly the **from** keyword or any keyword of this set.

General error handling
++++++++++++++++++++++

This part concerns error handling which does not take into account the type of the raised exception. It just let you define strategies that are used for any raised exception:

.. figure:: ../grammar-diagrams/handler-4.svg
   :align: center
   :scale: 100 %
   :alt: general error-handling

   the four error-handling policy we will have a look at and their configuration.

DeadLetter channel
~~~~~~~~~~~~~~~~~~

The **deadletter** will send received exception to the defined endpoint.

.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/HandlerSpec.scala#doc:babel-camel-deadletter

LoggingErrorHandler
~~~~~~~~~~~~~~~~~~~

The ``LoggingErrorHandler`` will just received exception to the defined logger at the defined logging level.

.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/HandlerSpec.scala#doc:babel-camel-loggingErrorHandler

DefaultErrorHandler
~~~~~~~~~~~~~~~~~~~

The default error handler is, as its name claims, implicit. This may be used to override inherited error handler.

.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/HandlerSpec.scala#doc:babel-camel-defaultErrorHandler

NoErrorHandler
~~~~~~~~~~~~~~

The NoErrorHandler overrides errorhandling defined by a route which is given as input of the current route.

.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/HandlerSpec.scala#doc:babel-camel-noErrorHandler

Exception clauses
+++++++++++++++++

You may define specific error management depending on the type of the thrown exception using the **on** keyword:

* **when** the exception should be treated, which is the parameter of the **on** keyword
* **continued**
* **handled**

.. figure:: ../grammar-diagrams/handler-3.svg
   :align: center
   :scale: 100 %
   :alt: OnException clauses

when
~~~~

The **when** parameter allows you to be more precise about the exception which should be process through this exception clause. In the below example, the exceptions which message contains "toto" are received in the output. The exceptions which message contains "tata" are handled but would not reach the output.

.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/HandlerSpec.scala#doc:babel-camel-exceptionClause-when


continued
~~~~~~~~~

The **continued** keyword allows to specify if the exchange should continue processing within the original route. The continued keyword accepts a ``Camel Predicate`` or a ``Boolean function``.

.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/HandlerSpec.scala#doc:babel-camel-exceptionClause-continued

handled
~~~~~~~
The **handled** keyword accepts a ``Camel Predicate`` or a ``Boolean function``. If the parameter to handled keyword evaluates to true then the ``exception`` will not be raised with a caller:

.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/HandlerSpec.scala#doc:babel-camel-exceptionClause-handled

Sub routes
~~~~~~~~~~

You may use the **sub** keyword to define a new route which should manage this exception

.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/HandlerSpec.scala#doc:babel-camel-exceptionClause-2

Configure several routes
++++++++++++++++++++++++

.. figure:: ../grammar-diagrams/handler-1.svg
   :align: center
   :scale: 100 %
   :alt: Error handling for Routes and RouteBuilder

   Every Error handling keyword may also be used for every route defined in the RouteBuilder by using the **handle** keyword as if it was the beginning of a route:

.. includecode:: ../../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/HandlerSpec.scala#doc:babel-camel-handleScope

In the example above, the two routes send their exceptions to the `mock:error` endpoint.