
Babel Installation Guide
========================


Project Requirements
++++++++++++++++++++

===========================     ========================================
Operating System                GNU/Linux, Mac OS X, MS Windows
Java VM                         Oracle JDK 6
Maven                           3.0.5
===========================     ========================================

.. _MavenInstall:

Build Babel with Maven
++++++++++++++++++++++

.. highlight:: bash

To build Babel from sources using `Apache Maven <http://maven.apache.org/>`_, in a regular shell::

  git clone https://github.com/babel-dsl/babel.git
  cd babel
  export MAVEN_OPTS="-XX:MaxPermSize=256m -Xmx1024m"
  mvn install

.. _ArchetypeInstall:

Build Babel Maven archetype
~~~~~~~~~~~~~~~~~~~~~~~~~~~

If you want to also install the Babel Camel Archetype locally, you only need to add the ``archetype`` maven profile to the previous script::

  git clone https://github.com/babel-dsl/babel.git
  cd babel
  export MAVEN_OPTS="-XX:MaxPermSize=256m -Xmx1024m"
  mvn -Parchetype install



Build Babel with Sbt
++++++++++++++++++++
To build Babel from sources using `Sbt <http://www.scala-sbt.org/>`_, in a regular shell::

  git clone https://github.com/babel-dsl/babel.git
  cd babel
  export SBT_OPTS="-XX:MaxPermSize=256m -Xmx1024m"
  sbt test publish-local

.. highlight:: scala

Generate the documentation
++++++++++++++++++++++++++

to generate the documentation, first install the sphinx documentation generator and then, run:
::

    git clone https://github.com/babel-dsl/babel.git
    cd babel/babel-doc
    make html
    #browse build/html/index.html

.. note::
  Akka provides some nice documentation on how to generate documentation with Sphinx: `Akka Sphinx Documentation <http://doc.akka.io/docs/akka/2.0/dev/documentation.html>`_
