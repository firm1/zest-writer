#!/bin/bash
ZDS_VERSION="v29.1a-plume"
NODE_VERSION="12"

source ./setup_node.sh

if [ -d "zds-site" ]; then
  rm -rf zds-site
fi

git clone  --branch "$ZDS_VERSION" https://github.com/zestedesavoir/zds-site.git

cd zds-site/

npm install
npm run build

cp -r dist ../../src/main/resources/com/zds/zw/assets/

cd ..

rm -rf zds-site