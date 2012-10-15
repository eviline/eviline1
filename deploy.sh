#!/bin/bash
mvn clean package jarsigner:sign
cp target/*.jar *.jnlp ../eviline_www/WebContent
cd ../eviline_www
git commit -a -m "Deploying"
git push

