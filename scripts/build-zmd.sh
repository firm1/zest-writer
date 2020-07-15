#!/bin/bash
NODE_VERSION="10.8.0"
NPM_VERSION="6.2.0"
ZMD_VERSION="8.3.0"

curl https://raw.githubusercontent.com/creationix/nvm/v0.30.2/install.sh | bash
source ~/.profile
nvm ls-remote

nvm install "$NODE_VERSION"
nvm use "$NODE_VERSION"

node -v

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