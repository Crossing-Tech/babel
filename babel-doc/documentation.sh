#To be called from the babel root directory

#Generation the scaladoc and the coverage report to be done
#export MAVEN_OPTS="-XX:MaxPermSize=256m -Xmx1024m"
#mvn -Pscaladoc -Pcoverage verify

cd babel-doc
make clean

#generate the main documentation
make html

#copy the generated report and scaladoc
mkdir  build/html/coverage/

cp -R ../babel-fish/target/site/scaladocs build/html/babel-fish 
cp -R ../babel-fish/target/classes/coverage-report/ build/html/coverage/babel-fish

cp -R ../babel-camel/babel-camel-core/target/site/scaladocs build/html/babel-camel-core
cp -R ../babel-camel/babel-camel-core/target/classes/coverage-report/ build/html/coverage/babel-camel-core

cp -R ../babel-camel/babel-camel-mock/target/site/scaladocs build/html/babel-camel-mock 
cp -R ../babel-camel/babel-camel-mock/target/classes/coverage-report/ build/html/coverage/babel-camel-mock

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

