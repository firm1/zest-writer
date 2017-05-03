#!/bin/bash

if [ -z "$1" ]
  then
    echo "Please give us APIKEY in argument"
    exit 1
fi

RPMFILE=`ls build/distributions/*.rpm`
APIKEY=$1
VERSION=`cat gradle.properties | grep "version" | cut -d "=" -f2`

echo "Upload du fichier $RPMFILE ..."

curl -T "$RPMFILE" -u "firm1:$APIKEY" "https://api.bintray.com/content/zest-writer/rpm/zest-writer/$VERSION/$RPMFILE?publish=1;override=0"

echo "Sign"

curl -u "firm1:$APIKEY" -X POST "https://api.bintray.com/calc_metadata/zest-writer/rpm"