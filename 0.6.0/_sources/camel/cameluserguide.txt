
.. _babel-camel-guide:

Babel Camel User Guide
======================

.. todo : be able to create a pdf only for babel camel user guide


In most examples, we are writing a `route` which represents the flow of your integration solution. Those `routes` are usually defined in a
`RouteBuilder`.
Most of the keywords are dealing with the Camel Message, unless specified as body specific (for example, **process** deals with a Camel Message and **processBody** deals with the body of the Camel Message).

.. warning::
  In the Message interface, its body is represented as an Option[T] to handle the nullable case. Thus, the keywords, such as **process**, which handles a Message should be aware of this point. The keywords, such as **processBody**, which handles directly the Message body do not have to care about that.

.. toctree::
  :maxdepth: 2

  spring
  samples
  basics
  transformation
  routing
  errormanagement

