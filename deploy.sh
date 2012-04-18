#!/bin/bash
ant
./sign_jar.sh
scp -r dist/* ../tetrevil_www/WebContent/* robin@www.tetrevil.org:/var/www/tetrevil

