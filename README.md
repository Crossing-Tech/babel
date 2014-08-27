Babel
=====

Master: [![Build Status](https://travis-ci.org/Crossing-Tech/babel.svg?branch=master)](https://travis-ci.org/Crossing-Tech/babel)

Babel is an efficient way to write your integration solution. It stands as a generic Domain Specific Language (DSL) especially maid for integration duties.

Implementation of Babel have been done for [Apache Camel](http://camel.apache.org "Apache Camel website").

### Project Requirements ###
 * Operarting System: GNU/Linux, Mac OS X, Windows
 * Java VM: Oracle JDK 6
 * Maven 3.0.5
 
### Project main dependencies ###
 * Scala library (2.10.4)
 * Apache camel (2.12.4)

### Quick Start Guide ###

#### Add Babel to an existing Maven project ####

To include Babel Camel in an existing Maven project, just add the following dependency in your pom.xml file (replacing BABEL_VERSION by the version you want to use):

```xml
<dependency>
  <groupId>io.xtech.babel</groupId>
  <artifactId>babel-camel-core</artifactId>
  <version>BABEL_VERSION</version>
</dependency>
```

If you are not installing the Babel project locally, you would also need to specify the Sonatype Snapshot repository in your Maven configuration:

```xml
<repository>
  <id>oss-sonatype</id>
  <name>oss-sonatype</name>
  <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
  <snapshots>
    <enabled>true</enabled>
  </snapshots>
</repository>
```


#### Create a new Maven project with Babel Camel ####

You may use the babel-camel-archetype in order to create a new Maven project. This project includes Babel dependencies and provide a default Babel Camel route. 
Currently, you need to first install Babel (following the next part of this document) as Babel is not yet deployed in any public repository (but coming soon).
Then, in a regular shell, just enter:
 
```
 mvn archetype:generate                       \
 -DarchetypeGroupId=io.xtech.babel            \
 -DarchetypeArtifactId=babel-camel-archetype
```

### Quick Installation Guide ###

#### Build Babel from sources ####
```
#in a regular shell:
git clone https://github.com/crossing-tech/babel.git
cd babel
export MAVEN_OPTS="-XX:MaxPermSize=256m -Xmx1024m"
mvn -Parchetype install
```

#### How to generate the documentation ####

To generate the documentation, you need to have [Sphinx](http://sphinx-doc.org/index.html `Sphinx website`) installed in your Python environment. Then just enter 
```
cd babel-doc
make html
```
to generate the main documentation in the build directory.

In order to generate the full babel documentation, you may use the `babel-doc/documentation.sh` script:
```
bash babel-doc/documentation.sh
```
and then browse the `babel-doc/build/html` directory.


Because of a bug with the Mac OS X Terminal (ValueError: unknown locale: UTF-8), you will maybe need to set these variables
```
export LC_ALL=en_US.UTF-8
export LANG=en_US.UTF-8
```
in your environment.
