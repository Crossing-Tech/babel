#To be called from the babel root directory

#Generation the scaladoc and the coverage report to be done
#export MAVEN_OPTS="-XX:MaxPermSize=256m -Xmx1024m"
#mvn -Pscaladoc -Pcoverage verify

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

########################################################################################################################
#To be done manually (as a step in a build system)
########################################################################################################################
#git fetch --all
#bash babel-doc/documentation.sh
#git checkout gh-pages
#git rm -rf .
#git reset HEAD .gitignore .nojekyll
#git checkout -- .gitignore .nojekyll
#cp -R  babel-doc/build/html/* .
#git add .
#git commit -m "documentation update" --author "Crossing-Tech SA <github@crossing-tech.com>"
#git push origin gh-pages
########################################################################################################################

