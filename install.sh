#!/bin/bash

LIB_FILES="target/lib/*.jar target/*.jar"

INSTALL_DIR="$(readlink -m $1)"
BIN_DIR="$INSTALL_DIR/bin"
DOC_DIR="$INSTALL_DIR/doc"
LIB_DIR="$INSTALL_DIR/lib"

if [ "$INSTALL_DIR" != "$PWD" ];
then
    mkdir -p "$INSTALL_DIR"
    rm -f -r "$BIN_DIR"
    mkdir -p "$BIN_DIR"
    rm -f -r "$LIB_DIR"
    mkdir -p "$LIB_DIR"

    cp -f -r $LIB_FILES "$LIB_DIR"
fi

./make-java-launcher.sh "$LIB_DIR" "$BIN_DIR"/rewrite-taxonomy       fr.inra.maiage.bibliome.util.taxonomy.dict.BuildDictionary
./make-java-launcher.sh "$LIB_DIR" "$BIN_DIR"/frequent-words         fr.inra.maiage.bibliome.util.count.FrequentWords
./make-java-launcher.sh "$LIB_DIR" "$BIN_DIR"/alvisae-diff-arabido   fr.inra.maiage.bibliome.util.alvisae.ArabidoDiff2013
./make-java-launcher.sh "$LIB_DIR" "$BIN_DIR"/alvisae-diff-biotopes  fr.inra.maiage.bibliome.util.alvisae.BiotopesDiff2013
./make-java-launcher.sh "$LIB_DIR" "$BIN_DIR"/tbrep                  fr.inra.maiage.bibliome.util.pattern.tabular.TabularPattern
./make-java-launcher.sh "$LIB_DIR" "$BIN_DIR"/sample                 fr.inra.maiage.bibliome.util.Sampler
./make-java-launcher.sh "$LIB_DIR" "$BIN_DIR"/select-pubmed          fr.inra.maiage.bibliome.util.pubmed.PubMedSelect
./make-java-launcher.sh "$LIB_DIR" "$BIN_DIR"/pubmed-index           fr.inra.maiage.bibliome.util.pubmed.PubMedIndexUpdater
./make-java-launcher.sh "$LIB_DIR" "$BIN_DIR"/pubmed-search          fr.inra.maiage.bibliome.util.pubmed.PubMedIndexSearcher
./make-java-launcher.sh "$LIB_DIR" "$BIN_DIR"/aggregate-table        fr.inra.maiage.bibliome.util.aggregate.TableAggregator
./make-java-launcher.sh "$LIB_DIR" "$BIN_DIR"/adjudication-stray-annotations fr.inra.maiage.bibliome.util.alvisae.AdjudicationStrayAnnotations
