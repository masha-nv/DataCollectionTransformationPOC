#!/bin/bash

SONAR_CLI_VERSION='4.5.0.2216-linux'
SONAR_SCANNER_FOLDER=".localsonar/sonar-scanner-$SONAR_CLI_VERSION"
set -e
mkdir .localsonar || true

if [ -d "$SONAR_SCANNER_FOLDER" ] 
then
    echo "Sonar CLI is already installed, continuing"
else
    echo "Sonar CLI is missing, installing"
    cd .localsonar/
    curl -o sonar-scanner.zip https://nexus.elis.nonprod.uscis.dhs.gov/repository/thirdparty/com/sonarqube/sonar-scanner/4.5.0.2216/sonar-scanner-4.5.0.2216.zip
    unzip -q sonar-scanner.zip
    cd ..
fi
export SONAR_USER_HOME=.localsonar/.sonar
if [ -z ${PULL_REQUEST_ID+x} ]; then
    echo "Running a CI scan for $SERVICE_VERSION"
    $SONAR_SCANNER_FOLDER/bin/sonar-scanner -Dsonar.projectVersion=$SERVICE_VERSION \
        -Dsonar.login=$JENKINS_SONAR_PASSWORD
else
    echo "Running a pull request scan for Pull Request: $PULL_REQUEST_ID"
    $SONAR_SCANNER_FOLDER/bin/sonar-scanner -Dsonar.login=$JENKINS_SONAR_PASSWORD \
        -Dsonar.pullrequest.key=${PULL_REQUEST_ID} \
        -Dsonar.pullrequest.branch=${PULL_REQUEST_BRANCH} \
        -Dsonar.pullrequest.base=${PULL_REQUEST_TARGET} \
        -Dsonar.pullrequest.github.repository=USCIS/react-beacon
fi
set +e