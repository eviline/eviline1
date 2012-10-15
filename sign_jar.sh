#!/bin/bash
jarsigner -keystore keystore -storepass `cat keystore_password` -signedjar target/eviline_signed.jar target/eviline.jar signFiles
