#!/bin/bash
jarsigner -keystore keystore -storepass `cat keystore_password` -signedjar dist/eviline_signed.jar dist/eviline.jar signFiles
