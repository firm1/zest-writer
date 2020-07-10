#!/bin/bash
ZMD_VERSION="8.3.0"

if [ -d "zmarkdown" ]; then
  rm -rf zmarkdown
fi

git clone  --branch "zmarkdown@$ZMD_VERSION" https://github.com/zestedesavoir/zmarkdown.git

cd zmarkdown/

npm install

cd packages/zmarkdown/

sed '/remark-iframes/d' -i package.json
sed '/remarkIframes/d' -i common.js

npm install

npm run release

cp -rp dist/*.js ../../../../src/main/resources/com/zds/zw/js/

cd ../../..

rm -rf zmarkdown