#!/bin/bash
nvm ls-remote

nvm install "$NODE_VERSION"
nvm use "$NODE_VERSION"

node -v
