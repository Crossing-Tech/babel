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

Currently, Babel has an provides an API on top of Apache Camel which may be used in Scala. Java API and other integration frameworks implementation would be implemented into the Babel experimental project (coming soon).

.. raw::  html

      <div class="about">
      <div class="left">
        <h3><img src="_images/modular.png" width="40px" /> Modular</h3>
        <p>Babel provides extension which may get composed in order to add specific functionalities.</p>
      </div>
      <div class="right">
        <h3><img src="_images/type.png" width="35px"/> Typed</h3>
        <p>Babel provides typing validation all along your routes.</p>
      </div>
      <span class="middle">
        <h3><img src="_images/lambda.png" width="40px"/> Functional</h3>
        <p>Babel allows you to use functions to configure your integration solution.</p>
      </span>
      <br />
        <div class="left">
        <h3><img src="_images/language.png" width="40px"/> Multi languages</h3>
        <p>Babel provides API for both Java and Scala.</p>
      </div>
      <div class="right">
        <h3><img src="_images/eip.png" width="35px"/> Integration</h3>
        <p>Babel aggregates main knowledge and technologies around EIPs such as modeled by Apache Camel and Spring Framework Integration </p>
      </div>
      <span class="middle">
        <h3><img src="_images/osgi.png" width="40px"/> OSGi integrated</h3>
        <p>Babel may run into OSGi environment and is also packaged for Apache Karaf.</p>
      </span>
      <br/>
      </div>

.. only:: html

   To get a rapid feeling over the main features provided by Babel, you may have a look at those slides: `Babel overview <slides/overview/index.html>`_ .

To use Babel on top of Camel, you may use the Babel Camel module. Please have a look to the :ref:`babel-camel-guide` for more details and examples.

.. includecode:: ../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/SortSpec.scala#doc:babel-camel-sort-1


.. toctree::
   :maxdepth: 1

   philosophy
   howtostart
   camel/cameluserguide
   devguide


.. note::
   In the following documentation,

   * Keywords are written such as **from**, **to** or **process**
   * Classes, packages, modules are written such as ``RouteBuilder`` or ``io.xtech.babel``





