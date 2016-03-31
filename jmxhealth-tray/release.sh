#!/bin/bash

if [ -z "$1" ]
  then
    echo "No release version supplied"
    exit -1
fi

VERSION="jmxhealth-tray-$1"
BASE_DIR="release/$VERSION"
APP_DIR="$BASE_DIR/app"
BIN_DIR="$BASE_DIR/bin"

if [ -d "$BASE_DIR" ]
  then
    echo "Cleaning Release Directory $BASE_DIR"
    rm -r "$BASE_DIR"
fi

mkdir "$BASE_DIR"
mkdir "$APP_DIR"
mkdir "$APP_DIR/node_modules"
mkdir "$BIN_DIR"

cp "package.json" "$APP_DIR/package.json"
cp "index.html" "$APP_DIR/index.html"
cp "emptyconfig.json" "$APP_DIR/config.json"
cp -r "target" "$APP_DIR/target"
cp -r "icons" "$APP_DIR/icons"
cp -r "node_modules/pubsub-js" "$APP_DIR/node_modules/pubsub-js"
cp nwjs/* "$APP_DIR/"

CURRENT_DIR=${PWD}

cd $APP_DIR
7z a -tzip "../bin/$VERSION.zip" "**"
cd $CURRENT_DIR
