#!/bin/bash


if [ -a ../lib/core.jar]
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
   -Dfile=$core \
   -DgroupId=org.lemurproject \
   -DartifactId=galago \
   -Dversion=3.13-SNAPSHOT \
   -Dpackaging=jar 
fi

