#!/bin/sh
#
# Deploy a jar, source jar, and javadoc jar to Sonatype's snapshot repo.
#
# Inspired by: https://github.com/square/okhttp

# In case we support more than one JDK versions, this is the one to be used
# when deploying the SNAPSHOTs
JDK="oraclejdk8"
# Only publish SNAPSHOTS from this branch
BRANCH="master"

set -e

if [ "$TRAVIS_JDK_VERSION" != "$JDK" ]; then
  echo "Skipping snapshot deployment: wrong JDK. Expected '$JDK' but was '$TRAVIS_JDK_VERSION'."
elif [ "$TRAVIS_PULL_REQUEST" != "false" ]; then
  echo "Skipping snapshot deployment: was pull request."
elif [ "$TRAVIS_BRANCH" != "$BRANCH" ]; then
  echo "Skipping snapshot deployment: wrong branch. Expected '$BRANCH' but was '$TRAVIS_BRANCH'."
else
  echo "Deploying snapshot..."
  mvn clean source:jar javadoc:jar deploy --settings="deploy/settings.xml" -Dmaven.test.skip=true
  echo "Snapshot deployed!"
fi
