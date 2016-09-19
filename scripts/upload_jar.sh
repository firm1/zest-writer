#!/bin/bash

if [ -z "$1" ]
  then
    echo "Please give us APIKEY in argument"
    exit 1
fi

DEBFILE=`ls build/libs/*-all-*.jar`
APIKEY=$1
VERSION=`cat gradle.properties | grep "version" | cut -d "=" -f2`

echo "Upload du fichier $DEBFILE ..."

curl -T "$DEBFILE" -u "firm1:$APIKEY" "https://api.bintray.com/content/firm1/maven/zest-writer/$VERSION/$DEBFILE"

echo "Sign"

curl -u "firm1:$APIKEY" -X POST "https://api.bintray.com/calc_metadata/firm1/maven/build/libs"
