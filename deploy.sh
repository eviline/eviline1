#!/bin/bash
ant
./sign_jar.sh
scp dist/* ../tetrevil_www/WebContent/* robin@www.tetrevil.org:/var/www/tetrevil

