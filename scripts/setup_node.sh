#!/bin/bash

curl https://raw.githubusercontent.com/creationix/nvm/v0.30.2/install.sh | bash
source ~/.profile

nvm ls-remote

nvm install "$NODE_VERSION"
nvm use "$NODE_VERSION"

node -v
