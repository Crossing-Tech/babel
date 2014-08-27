
Babel Installation Guide
========================


Project Requirements
++++++++++++++++++++

===========================     ========================================
Operating System                GNU/Linux, Mac OS X, MS Windows
Java VM                         Oracle JDK 6
Maven                           3.0.5
===========================     ========================================


Build Babel from sources
++++++++++++++++++++++++

.. highlight:: bash

To build Babel from sources, in a regular shell::

  git clone https://github.com/babel-dsl/babel.git
  cd babel
  export MAVEN_OPTS="-XX:MaxPermSize=256m -Xmx1024m"
  mvn install

.. highlight:: scala

Generate the documentation
++++++++++++++++++++++++++

to generate the documentation, first install the sphinx documentation generator and then, run:
::

    make html

.. note::
  Akka provides some nice documentation on how to generate documentation with Sphinx: `Akka Sphinx Documentation <http://doc.akka.io/docs/akka/2.0/dev/documentation.html>`_
