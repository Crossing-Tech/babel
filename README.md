Babel
=====

### Build Status ###

Master: [![Build Status](https://travis-ci.org/Crossing-Tech/babel.svg?branch=master)](https://travis-ci.org/Crossing-Tech/babel) [![Coverage Status](https://coveralls.io/repos/Crossing-Tech/babel/badge.svg?branch=master)](https://coveralls.io/r/Crossing-Tech/babel?branch=master)

### Description ###

Babel is an efficient way to write your integration solution. It stands as a generic Domain Specific Language (DSL) especially made for integration duties.

Implementation of Babel have been done for [Apache Camel](http://camel.apache.org "Apache Camel website").

### Communication ###

 * [Babel documentation](http://crossing-tech.github.io/babel)
 * Google Group [Babel User List](https://groups.google.com/forum/#!forum/babel-user)
 * IRC: irc.codehaus.org:6697 #babel

### Project Requirements ###
 * Operating System: GNU/Linux, Mac OS X, Windows
 * Java VM: Oracle JDK 6
 * Sbt 0.13
 
### Project main dependencies ###
 * Scala library (2.14.4)
 * Apache camel (2.15.0)
 
#### Backward compatibity ####
  
Babel provides also artifacts for earlier versions of Apache Camel:

To use Apache Camel version 2.12.x, use Babel with version 0.7.0-camel-2.12.4
 
### Quick Sbt Start Guide ###

#### Add Babel to an existing sbt project ####

To include Babel Camel in an existing Sbt project, just add the following dependency in your configuration file (replacing BABEL_VERSION by the version you want to use):

```libraryDependencies += "io.xtech.babel" %% "babel-camel-core" % "BABEL_VERSION"```

If you are not installing the Babel project locally, you would also need to specify the Sonatype Snapshot repository in your project configuration:

```resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"```

### Quick Maven Start Guide ###

#### Add Babel to an existing Maven project ####

To include Babel Camel in an existing Maven project, just add the following dependency in your pom.xml file (replacing BABEL_VERSION by the version you want to use):

```xml
<dependency>
  <groupId>io.xtech.babel</groupId>
  <artifactId>babel-camel-core</artifactId>
  <version>BABEL_VERSION</version>
</dependency>
```


### Quick Installation Guide ###

#### Build Babel from sources with Sbt ####
```
#in a regular shell:
git clone https://github.com/crossing-tech/babel.git
cd babel
export SBT_OPTS="-XX:MaxPermSize=256m -Xmx1024m"
sbt test publish-local 
#or if you want to publish artifacts for maven
sbt test publish-m2 
```

#### Build Babel from sources with Maven ####
```
#in a regular shell:
git clone https://github.com/crossing-tech/babel.git
cd babel
export MAVEN_OPTS="-XX:MaxPermSize=256m -Xmx1024m"
mvn -Parchetype install
```
