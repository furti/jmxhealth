#!/bin/bash

if [ -z "$1" ]
  then
    echo "No release version supplied"
    exit -1
fi

echo $1
sed -i 's/.*\"version\".*/  \"version\": \"'$1'\",/' package.json

VERSION="jmxhealth-tray-$1"
BASE_DIR="release/$VERSION"
APP_DIR="$BASE_DIR/app"
BIN_DIR="$BASE_DIR/bin"
CURRENT_DIR=${PWD}

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
cp "tray-popup.html" "$APP_DIR/tray-popup.html"
cp "emptyconfig.json" "$APP_DIR/config.json"
cp -r "target" "$APP_DIR/target"
cp -r "icons" "$APP_DIR/icons"
cp -r "node_modules/pubsub-js" "$APP_DIR/node_modules/pubsub-js"

cd $APP_DIR
7z a "../bin/$VERSION.zip" "**"
cd $CURRENT_DIR

cp -r nwjs/release/* "$APP_DIR/"
rm "$APP_DIR/nwjc.exe"

cd $APP_DIR
7z a -t7z -mx9 "../bin/$VERSION-standalone.7z" "**"
cd $CURRENT_DIR
