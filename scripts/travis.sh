#!/bin/bash

if [ "$TRAVIS_PULL_REQUEST" != "false" ] && [ -n "${GITHUB_TOKEN:-}" ]; then
    echo 'Internal pull request: trigger QA and analysis'

    ./gradlew sonarqube \
        -Dzw.username=$ZDS_USERNAME \
        -Dzw.password=$ZDS_PASSWORD \
        -Dzw.github_user=$GITHUB_USER \
        -Dzw.github_token=$GITHUB_TOKEN \
        -Dsonar.login=$SONAR_TOKEN \
        -Dsonar.analysis.mode=preview \
        -Dsonar.github.pullRequest=$TRAVIS_PULL_REQUEST \
        -Dsonar.github.repository=$TRAVIS_REPO_SLUG \
        -Dsonar.github.oauth=$GITHUB_TOKEN \
        -Dsonar.host.url=https://sonar.winxaito.com
else
    ./gradlew sonarqube --debug \
            -Dzw.username=$ZDS_USERNAME \
            -Dzw.password=$ZDS_PASSWORD \
            -Dzw.github_user=$GITHUB_USER \
            -Dzw.github_token=$GITHUB_TOKEN \
            -Dsonar.login=$SONAR_TOKEN \
            -Dsonar.host.url=https://sonar.winxaito.com
fi
