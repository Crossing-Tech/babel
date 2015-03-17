.. _babel-quick-start:

Quick start guide
=================

Babel depends mainly on the following artifacts:

* org.scala-lang/scala-library/2.10.4
* org.apache.camel/camel-core/2.12.4

Using Maven
+++++++++++

.. highlight:: xml

To add Babel Camel in a Maven project, just add the following dependencies to your pom.xml file:

::

   <dependency>
     <groupId>io.xtech.babel</groupId>
     <artifactId>babel-camel-core</artifactId>
     <version>BABEL_VERSION</version>
   </dependency>

If you don't want to build the Babel project on your machine (otherwise, see :ref:`MavenInstall`), use the Sonatype Snapshot repository to your Maven configuration::

   <repository>
        <id>oss-sonatype</id>
        <name>oss-sonatype</name>
        <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>

If you also want to use the Babel Camel Mock extension for your tests, you may also add:

::

   <dependency>
     <groupId>io.xtech.babel</groupId>
     <artifactId>babel-camel-mock</artifactId>
     <version>BABEL_VERSION</version>
     <scope>test</scope>
   </dependency>

Where BABEL_VERSION is replaced by the actual Babel version.

Using Sbt
+++++++++

.. highlight:: scala

To add Babel Camel in a Sbt project, you may just add the following dependencies and resolver to your build configuration:

::

  resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots

  libraryDependencies += "io.xtech.babel" %% "babel-camel-core" % "BABEL_VERSION"
  //if you want to use the mock keyword to simplify your tests:
  libraryDependencies += "io.xtech.babel" %% "babel-camel-mock" % "BABEL_VERSION" % "test"

Where BABEL_VERSION is replaced by the actual Babel version.

Babel Archetypes
++++++++++++++++

The Babel Camel Archetype may help you to start a maven project using Babel. Currently, you need to first install the Babel project with Maven following the :ref:`ArchetypeInstall` part.
Once you have installed the babel project locally, use the following command to start a new maven project including Babel through the following command::

  mvn archetype:generate                       \
  -DarchetypeGroupId=io.xtech.babel            \
  -DarchetypeArtifactId=babel-camel-archetype
