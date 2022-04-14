#!/bin/bash

for package in root base-events coa config federate-base federation-manager fedmanager-host; do

    echo "PLEASE REMOVE THE FOLLOWING PACKAGE FROM YOUR MAVEN REPOSITORY:"
    echo
    echo "org.cpswt.$package"
    echo
    echo -n "PRESS ENTER ONCE THIS PACKAGE HAS BEEN REMOVED: "
    read dummy
    echo

    COMMAND="./gradlew :$package:build --rerun-tasks --refresh-dependencies"
    echo "Executing \"$COMMAND\"":
    $COMMAND
    if [ $? -ne 0 ]; then
        echo "COMMAND \"$COMMAND\" FAILED.  PLEASE FIX THE PROBLEM AND RERUN THIS SCRIPT"
        exit 1
    fi
    echo
    COMMAND="./gradlew :$package:publish"
    $COMMAND
    if [ $? -ne 0 ]; then
        echo "COMMAND \"$COMMAND\" FAILED.  PLEASE FIX THE PROBLEM AND RERUN THIS SCRIPT"
        exit 2
    fi
    echo

done

echo "DONE"
