#!/bin/bash

if [ -z "$1" ]
  then
    echo "Please give us APIKEY in argument"
    exit 1
fi


RPMFILE=`ls build/distributions/*.rpm`
APIKEY=$1
VERSION=`cat gradle.properties | grep "version" | cut -d "=" -f2`

echo "Upload du fichier $DEBFILE ..."

curl -T "$RPMFILE" -u "firm1:$APIKEY" "https://api.bintray.com/content/firm1/rpm/zest-writer/$VERSION/$RPMFILE"
