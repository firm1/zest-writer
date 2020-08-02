#!/bin/bash

if [ -z "$1" ]
  then
    echo "Please give us APIKEY in argument"
    exit 1
fi

DMGFILE=`ls build/installer/*.dmg`
APIKEY=$1
VERSION=`cat gradle.properties | grep "version" | cut -d "=" -f2`

echo "Upload du fichier $DMGFILE ..."

curl -T "$DMGFILE" -u "firm1:$APIKEY" "https://api.bintray.com/content/zest-writer/dmg/zest-writer/$VERSION/$DMGFILE?publish=1;override=0"
