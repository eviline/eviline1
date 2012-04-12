#!/bin/bash
jarsigner -keystore keystore -storepass `cat keystore_password` -signedjar dist/tetrevil_signed.jar dist/tetrevil.jar signFiles
