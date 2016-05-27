#!/bin/bash

set -ex

APP_VERSION=`grep 'SNAPSHOT</version>' pom.xml | sed -r 's/.*<version>(.+)-.*/\1/'` 
TIME_STAMP=$(date +%Y%m%d.%H%M%S)
echo "APP_VERSION=${APP_VERSION}" >> version.properties
echo "TIME_STAMP=${TIME_STAMP}" >> version.properties
