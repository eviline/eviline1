#!/bin/bash
ant
./sign_jar.sh
scp dist/* www/* robin@www.tetrevil.org:/var/www/tetrevil

