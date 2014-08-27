Babel
=====

Master: [![Build Status](https://travis-ci.org/Crossing-Tech/babel.svg?branch=master)](https://travis-ci.org/Crossing-Tech/babel)

Babel is an efficient way to write your integration solution. It stands as a generic Domain Specific Language (DSL) especially maid for integration duties.

Implementation of Babel have been done for the following frameworks, which allows you to uses those while writing Babel code:

 * [Apache Camel](http://camel.apache.org "Apache Camel website")
 * [Spring Framework Integration](http://projects.spring.io/spring-integration/ "Spring Framework Integration website")

### Project Requirements
 * Operarting System: GNU/Linux, Mac OS X
 * Java VM: Oracle JDK 6
 * Maven 3.0.5

### Quick Installation Guide ###

#### Build Babel from sources ####
```
#in a regular shell:
user@host$ git clone https://github.com/crossing-tech/babel.git
user@host$ cd babel
user@host$ export MAVEN_OPTS="-XX:MaxPermSize=256m -Xmx1024m"
user@host$ mvn install
```

#### How to generate the documentation ####

To generate the documentation, you need to have [Sphinx](http://sphinx-doc.org/index.html `Sphinx website`) installed in your Python environment. Then just enter 
```
make html
```
to generate the documentation in the build directory.


Because of a bug with the Mac OS X Terminal (ValueError: unknown locale: UTF-8), you will maybe need to set these variables
```
export LC_ALL=en_US.UTF-8
export LANG=en_US.UTF-8
```
in your environment.

#### How to generate the Railroad diagrams for the documentation ####

The Railroad diagrams generation is managed through the **grammar** maven module. It provides a **GrammarParser** object which takes 2 parameters:

* the directory to be scanned for grammar files (with .grammar extension)
* the directory where the generated diagrams (with .svg extension) should be written.

For more details on the grammar, please have a look at the grammar module or to the [RRDiagram project](https://github.com/Chrriis/RRDiagram) on GitHub.
