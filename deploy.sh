#!/bin/bash
mvn package
./sign_jar.sh
cp target/*.jar *.jnlp ../eviline_www/WebContent
cd ../eviline_www
git commit -a -m "Deploying"
git push

