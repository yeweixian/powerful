#!/bin/sh

./mvnw clean install -Dmaven.test.skip=true
echo "------> done."
