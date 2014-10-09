.. _babel-opinion:

Babel Opinion
=============

Babel opinion is that code which defines integration should be easy to write and to read. From the experience we have got using Apache Camel (Java DSL), we have raise the following points:

- A DSL needs to be simple to make it easy to write
- A DSL needs to be explicit to make it easy to read

We have been influenced by the `Scala <www.scala-lang.org/>`_ language. In other words, we just want to have the same features we already had with Scala at the route layer. Let's take a look at those points:

Simple DSL
++++++++++

Simple means we would provide one way to do things. This means selection. Currently, the amount of possibilities does not facilitate the coding process.

Actually, "simple" means composed by one thing, without duplicity nor ornamentation but plain. At this point, simple means also easy in the sense we have tried to restrict the possibilities to the most interesting. Babel adds as less as possible new Interfaces and tries as much as possible to rely on existing and basic tools such as Functions. As we will see latter, clear auto-completion provided by your IDE increase the coding process and adds confidence into what you are actually writing. Select the best way to achieve your task and then *transform a good practice into API* could be the main goal of the Babel project.

Explicit DSL
++++++++++++

As a first taste, explicit means to avoid some implicits. In Apache Camel, an implict we found really dangerous and not advantageous was the Implict type converters: Apache Camel implicitely add type conversion depending on your code expectations.
In other words, with Apache Camel, if your code expects some type concerning its input which is not the output of the previous step, this will be fixed implicitely at runtime.
On the contrary, with Babel, the input type is as explicit as possible. For convenience, it is simply computed from the previous step output. Even if you do not see it in your code, your IDE may compute it at compile time and this is used to validate your code before having actually run it.

More important is the fact that adding information about the input type helps to read your code (and also to review it). Babel tends also to separate (explicit) type conversion from input transformation.

Explicit and Simple DSL
+++++++++++++++++++++++

Now let's have a look to what does those two feature provide together to the coding process. Actually, we may also resume those two aspects as **typed** and **functional**.

In other words, Babel lets you define processing in functional way and raise Scala type inference at the route level.

Auto completion takes advantages from the typing as much as from the simplicity:

- Having only the possible keywords in completion helps to find the good one
- Type inference checks and provides inputs type to your functions

