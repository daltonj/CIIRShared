#!/bin/bash

if [ -a ../lib/core.jar ]
then
    galago=../lib/core.jar

elif [ -a ./lib/core.jar ]
then
    galago=./lib/core.jar
fi

if [ -z $galago ]
then
    echo "Can not find core.jar file: it should be in lib directory."
else
    mvn install:install-file \
   -Dfile=$galago \
   -DgroupId=org.lemurproject.galago \
   -DartifactId=core \
   -Dversion=3.13-SNAPSHOT \
   -Dpackaging=jar 
fi

