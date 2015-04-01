#!/bin/bash

#How to use it: just call this script from the project root directory.

# Setup if you want to use virtual python environment
#virtualenv ENV
#source ./ENV/bin/activate

pip install sphinx sphinx_bootstrap_theme

cd babel-doc
make clean

#generate the main documentation
make html

#generate the main documentation in pdf
make latexpdf
cp build/latex/Babel.pdf build/html/_static/

#Clean the python virtual env
cd ..

#cleaning of the virtual python environment
# deactivate 

