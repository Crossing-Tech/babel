
Babel Philosophy
================

Babel way of thinking is to use the functional and typed way to write integration code. This vision is clearly taken from the `Scala <www.scala-lang.org/>`_ language. We have summarized this vision in the following 3 topics:

- Babel is a typed DSL
- Babel is a functional DSL
- Babel is a simple DSL

Babel little story
++++++++++++++++++

Babel, in its conception, was inspired by the way Java developer may improve their code using the Scala language.
First, adding stricter management of the types provides validation at compile time which means less required tests and more powerful IDE.
Secondly, using directly the functions reduces the amount of code for each simple implementation.

Those two features, once correctly added together, provide clean and explicit code semantics. As we were implementing solution using the Camel Java DSL, we felt such improvement was also possible on a really determined domain such as integration using Camel.
Moreover, we had encountered several implementations where the Camel possibilities were just let aside, developers prefer to implement a large number of Camel features by themselves as they had not felt comfortable enough to do it through the Camel Java DSL.

Integration definition should be as easy as possible to let the complexity where it is effectively: the integration with other systems.

Babel has grown and we experiment it using other integration frameworks such as Spring Framework Integration. We also tends to provide a Java API for the Babel Camel DSL, but the main focus of the project has been done on the Babel Camel DSL for Scala.

Babel as a typed DSL
++++++++++++++++++++

Babel provides typing of the exchange payload (called "exchange body" or simply "body") in Camel routes. This means, when you look at your Babel Camel route source code, at each step, you may infer what is the type of the body while entering an endpoint or a processor (which actually means any keyword, by the way). Do not be afraid, your IDE will be able to infer the type for you.
This provides actually 3 interesting features:

* The compiler would try to validate your Babel Camel route typing sequence. Of course, the compiler already checks you are using existing keywords in your route, but with Babel, the compiler also check if the type is correctly transformed during the flow of the route definition.
* Camel would not require implicit and hidden type transformation. Babel advises you to be as explicit as possible with regard to the type of the exchange body. You may of course ask why is it a bad thing letting Camel do this job for you. Then, you may think how Camel is defining how to transform the body from one type to another. Well, you actually don't know because Camel is defining this dynamically at runtime and may select randomly the type transformation when encountering several transformations corresponding to your requirements.
* Types is a great way to communicate, to define protocols or even to define a contract between developers.

Finally, the fact that the exchange body  is typed provides a great and powerful base for functional programming as we will see in the next chapter.

.. includecode:: ../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/sample/SamplePhilosophySpec.scala#doc:babel-camel-sample-2


Babel as a functional DSL
+++++++++++++++++++++++++

Do you really require defining a ``Processor`` class just to provide a ``process`` method to your route? Well, it's not only the fact we may save 2 lines of code but also to improve the meaning of each statement.
A transformation may be represented through an object (and Babel also allows such semantic), but a transformation is, in essence, functions.

Moreover, combining the typed and the functional aspect of Babel ensures that you provide the correct type directly to what will do the transformation. You may think about that just as coming back to an API, with simple method calls.

.. includecode:: ../../babel-camel/babel-camel-core/src/test/scala/io/xtech/babel/camel/sample/SamplePhilosophySpec.scala#doc:babel-camel-sample-3

Babel as a simple DSL
+++++++++++++++++++++

Finally, based on the experience we had with Camel Java DSL, we have been through some difficulties while choosing between several possible implementations. Camel has a great (even a greatest) number of configurations and we would say most of them are overlapping.
One of the most important feature of Camel, its wide range of possibilities, may also be a difficulty for those who, trying to learn the language, have difficulties to choose how to implement their use case. The main goal of Babel is to ensure writing an integration solution is as simple as possible, knowing the main difficulties are expected to be elsewhere.

