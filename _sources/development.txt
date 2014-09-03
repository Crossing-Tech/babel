
Babel development
=================

.. highlight:: bash

We will define here some guide lines used during the development of Babel.

Build
+++++

Babel is build using Apache Maven (even if a Sbt configuration is synchronized with).

Source Code
+++++++++++

The Code is reformatted by the Maven reformat profile (which uses `scalariform <https://github.com/sbt/sbt-scalariform>`_)
To reformat the code:
::

   mvn -Preformat process-sources

Tests
+++++

In the Babel modules implemented using Scala, `Specs2 <http://etorreborre.github.io/specs2/>`_ is the used testing framework.

Test coverage reports are generated using scoverage through the Maven build configuration following:
::

   mvn -Pcoverage verify
   #browse test reports in modules target directory (target/classes/coverage-report)

Deployment
+++++++++++

Snapshots are deployed using the Maven build configuration as following:
::

   mvn -Parchetype deploy


Release
+++++++

Releases are done using the Maven build configuration (coming soon)


.. highlight:: scala