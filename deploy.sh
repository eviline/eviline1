#!/bin/bash
ant
./sign_jar.sh
#scp -r dist/* ../tetrevil_www/WebContent/* robin@www.tetrevil.org:/var/www/tetrevil
cp dist/* ../tetrevil_www/WebContent
cd ../tetrevil_www
git commit -a -m "Deploying"
git push

