#!/bin/bash

curl https://raw.githubusercontent.com/creationix/nvm/v0.30.2/install.sh --output /tmp/install.sh
source /tmp/install.sh
source ~/.nvm/nvm.sh
nvm ls-remote
nvm install "$NODE_VERSION"
nvm use "$NODE_VERSION"
node -v