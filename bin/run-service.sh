#!/usr/bin/env bash

SCRIPT_NAME=$0

if [[ "$SCRIPT_NAME" = /* ]] || [[ $SCRIPT_NAME =~ ^[A-z]:\\ ]]
then
    ROOT_PROJECT_DIR=$(dirname $SCRIPT_NAME)
else
    ROOT_PROJECT_DIR=$(dirname "$(pwd)/$SCRIPT_NAME")
fi

ROOT_PROJECT_DIR=$(dirname $ROOT_PROJECT_DIR)

set -xe
java -agentlib:jdwp=transport=dt_socket,address=5005,server=y,suspend=n -jar "$ROOT_PROJECT_DIR/build/libs/mvctest-1.0-SNAPSHOT.jar"