#!/bin/bash

if [ -z "$1" ]
  then
    echo "Please give us APIKEY in argument"
    exit 1
fi

DEBFILE=`ls build/distributions/*.deb`
APIKEY=$1
VERSION=`cat gradle.properties | grep "version" | cut -d "=" -f2`

echo "Upload du fichier $DEBFILE ..."

curl -T "$DEBFILE" -u "firm1:$APIKEY" "https://api.bintray.com/content/firm1/deb/zest-writer/$VERSION/$DEBFILE;deb_distribution=wheezy;deb_component=main;deb_architecture=i386,amd64"
