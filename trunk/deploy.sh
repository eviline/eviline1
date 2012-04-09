#!/bin/bash
ant
./sign_jar.sh
scp dist/* robin@www.tetrevil.org:/var/www/tetrevil

