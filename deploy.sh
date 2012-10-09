#!/bin/bash
ant
./sign_jar.sh
cp dist/* ../eviline_www/WebContent
cd ../eviline_www
git commit -a -m "Deploying"
git push

