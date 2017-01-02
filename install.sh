#!/bin/bash

DOC_FILES="doc/html"
LIB_FILES="lib/*.jar"

INSTALL_DIR="$(readlink -m $1)"
BIN_DIR="$INSTALL_DIR/bin"
DOC_DIR="$INSTALL_DIR/doc"
LIB_DIR="$INSTALL_DIR/lib"

if [ "$INSTALL_DIR" != "$PWD" ];
then
    mkdir -p "$INSTALL_DIR"
    rm -f -r "$BIN_DIR"
    mkdir "$BIN_DIR"
    rm -f -r "$DOC_DIR"
    mkdir "$DOC_DIR"
    rm -f -r "$LIB_DIR"
    mkdir "$LIB_DIR"

    cp -f -r $DOC_FILES "$DOC_DIR"
    cp -f -r $LIB_FILES "$LIB_DIR"
fi

./make-java-launcher.sh "$LIB_DIR" "$BIN_DIR"/rewrite-taxonomy       org.bibliome.util.taxonomy.dict.BuildDictionary
./make-java-launcher.sh "$LIB_DIR" "$BIN_DIR"/frequent-words         org.bibliome.util.count.FrequentWords
./make-java-launcher.sh "$LIB_DIR" "$BIN_DIR"/alvisae-diff-arabido   org.bibliome.util.alvisae.ArabidoDiff2013
./make-java-launcher.sh "$LIB_DIR" "$BIN_DIR"/alvisae-diff-biotopes  org.bibliome.util.alvisae.BiotopesDiff2013
./make-java-launcher.sh "$LIB_DIR" "$BIN_DIR"/tbrep                  org.bibliome.util.pattern.tabular.TabularPattern
./make-java-launcher.sh "$LIB_DIR" "$BIN_DIR"/sample                 org.bibliome.util.Sampler
