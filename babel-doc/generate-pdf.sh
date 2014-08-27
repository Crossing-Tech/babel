#!/bin/bash

#generate the documentation in latex format
make latex

#generate grammar diagrams in png format
for file in source/grammar-diagrams/*.svg
do
  output=$(basename $file .svg).png
  convert $file $output
  mv $output build/latex/
done

#change tex images from svg to be png
sed -e 's/svg/png/g' build/latex/Babel.tex > temp.tex
mv temp.tex build/latex/Babel.tex

#generate the pdf
cd build/latex
pdflatex *.tex
pdflatex *.tex #done twice for table of content
