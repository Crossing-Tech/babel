
Babel development
=================

.. highlight:: bash

We will define here some guide lines used during the development of Babel.

Build
+++++

Babel is build using Apache Maven (even if a Sbt configuration is synchronized with).

Source Code
+++++++++++

The Code is reformatted while compiling the sources using sbt (which uses `scalariform <https://github.com/sbt/sbt-scalariform>`_)
To reformat the code:
::

   sbt test-compile

Tests
+++++

In the Babel modules implemented using Scala, `Specs2 <http://etorreborre.github.io/specs2/>`_ is the used testing framework.

Test coverage reports are generated using scoverage through the sbt build configuration following:
::

   sbt scoverage:test
   #browse test reports in modules target directory (target/classes/coverage-report)

Deployment
+++++++++++

With Maven
~~~~~~~~~~
Snapshots are deployed using the Maven build configuration as following:
::

   mvn -Parchetype deploy

With Sbt
~~~~~~~~
Snapshots are deployed using the Sbt build configuration as following:
::

   sbt publish-local

Maven archetype won't be installed using this command.

Release
+++++++

Releases are done using the Sbt/Maven build configuration (coming soon)


.. highlight:: scala