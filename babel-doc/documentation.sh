#!/bin/bash

#How to use it: just call this script from the project root directory.

# Setup if you want to use virtual python environment
#virtualenv ENV
#source ./ENV/bin/activate

pip install sphinx sphinx_bootstrap_theme
#concerning the slides
pip install sphinxjp.themecore sphinxjp.themes.impressjs

cd babel-doc
make clean

#generate the main documentation
make html

#generate the slides
cd slides/overview
make html
cd ../..
mkdir build/html/slides
cp -R slides/overview/_build/html build/html/slides/overview

#Clean the python virtual env
cd ..

#cleaning of the virtual python environment
# deactivate 

