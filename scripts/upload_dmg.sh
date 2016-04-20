#!/bin/bash

if [ -z "$1" ]
  then
    echo "Please give us APIKEY in argument"
    exit 1
fi

DMGFILE=`ls build/distributions/*.deb`
APIKEY=$1
VERSION=`cat gradle.properties | grep "version" | cut -d "=" -f2`

echo "Upload du fichier $DMGFILE ..."

curl -T "$DMGFILE" -u "firm1:$APIKEY" "https://api.bintray.com/content/firm1/dmg/zest-writer/$VERSION"
