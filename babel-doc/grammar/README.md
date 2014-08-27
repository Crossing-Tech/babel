Babel grammar diagram generator
===============================

This module provide some object in order to generate Railroad (syntax) diagrams in svg files. 

BNF Syntax
----------
The supported BNF subset when reading is the following:

* definition
    * =
    * :=
    * ::=
* concatenation
    * ,
    * \<space\>
* termination
    * ;
* alternation
    * |
* option
    * [ ... ]
    * ?
* repetition
    * { ... } =\> 0..N
    * expression* =\> 0..N
    * expression+ =\> 1..N
    * \<digits\> * expression =\> \<digits\>...\<digits\>
    * \<digits\> * [expression] =\> \<0\>...\<digits\>
    * \<digits\> * expression? =\> \<0\>...\<digits\>
* grouping
     * ( ... )
* literal
     * " ... " or ' ... '
* special characters
    *  (? ... ?)
*  comments
    * (\* ... \*)

Please have a look to the [used library](https://github.com/Chrriis/RRDiagram) for any update or information :

