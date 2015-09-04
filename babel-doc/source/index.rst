.. Babel documentation master file, created by
   sphinx-quickstart on Mon May 12 12:28:25 2014.
   You can adapt this file completely to your liking, but it should at least
   contain the root `toctree` directive.

Welcome to Babel's documentation!
=================================

.. only:: html

   .. image:: ../images/logo-without-pole.png

Babel is an elegant way to write your integration solution. It tries to provide as much as possible validation during the definition of your integration solution in order to avoid time spent into testing or deploying invalid code.

Babel is a layer on top of the main integration frameworks and may be used from Scala and Java source code. The following documentation should guide you into your journey toward a new way to write integration in a secure and efficient way.

Currently, Babel provides an API on top of Apache Camel which may be used in Scala. Java API and other integration frameworks implementation would be implemented into the `Babel experimental <https://github.com/crossing-tech/babel-experimental>`_ project.

.. raw::  html

      <div class="about">
      <div class="left">
        <h3><img src="_static/modular.png" width="40px" /> Modular</h3>
        <p>Babel provides extension which may get composed in order to add specific functionalities.</p>
      </div>
      <div class="right">
        <h3><img src="_static/type.png" width="35px"/> Typed</h3>
        <p>Babel provides typing validation all along your routes.</p>
      </div>
      <span class="middle">
        <h3><img src="_static/lambda.png" width="40px"/> Functional</h3>
        <p>Babel allows you to use functions to configure your integration solution.</p>
      </span>
      <br />
        <div class="left">
        <h3><img src="_static/language.png" width="40px"/> Multi languages</h3>
        <p>Babel provides API for both Java and Scala.</p>
      </div>
      <div class="right">
        <h3><img src="_static/eip.png" width="35px"/> Integration</h3>
        <p>Babel aggregates main knowledge and technologies around EIPs such as modeled by Apache Camel and Spring Framework Integration </p>
      </div>
      <span class="middle">
        <h3><img src="_static/osgi.png" width="40px"/> OSGi integrated</h3>
        <p>Babel may run into OSGi environment and is also packaged for Apache Karaf.</p>
      </span>
      <br/>
      </div>

.. only:: html

   This documentation concerns the version 0.8.0. You may also access the `previous released version <http://crossing-tech.github.io/babel/0.7.0/index.html>`_ documentation.


To use Babel on top of Camel, you may use the Babel Camel module. Please have a look to the :ref:`babel-quick-start` and to the :ref:`babel-camel-guide` for more details and examples.

To have a better intuition of what is Babel, you would find it in the :ref:`babel-opinion` and the :ref:`babel-architecture` pages.

In the following code snippet, we compare Babel and Camel Scala DSL. Those two routes are summing the a list of number, provided as a String and routing this sum depending on its positivity.

.. only:: html

   ==========================================================================================================================================   ==========================================================================================================================================
   **Babel sample**                                                                                                                             **Camel scala sample**
   .. includecode:: ../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/choice/DemoSpec.scala#doc:babel-camel-demo-1         .. includecode:: ../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/choice/DemoSpec.scala#doc:babel-camel-demo-scala-1
   Concerning the route structure, the main aspect is the fact we need to repeat the type everywhere. Moreover, you may also use pure functions in the Babel Code where you need mutability in the Camel Scala process method call. Let's have a look at how aggregation is configured:
   ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


   .. includecode:: ../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/choice/DemoSpec.scala#doc:babel-camel-demo-2         .. includecode:: ../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/choice/DemoSpec.scala#doc:babel-camel-demo-scala-2
   Babel Camel has add simple interfaces in order to simplify and concentrate your code: The trivial behavior for first and last input is more clear using a Reduce or a Fold pattern. Moreover, the correlation and the completion is define in a single place which makes the aggregation more uniform.
   ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

   ==========================================================================================================================================   ==========================================================================================================================================


.. note::
In the following documentation,

   * Keywords are written such as **from**, **to** or **process**
   * Classes, packages, modules are written such as ``RouteBuilder`` or ``io.xtech.babel``

In the following sections,

.. toctree::
   :maxdepth: 1

   opinion
   architecture
   quickstartguide
   cameluserguide
   devguide








