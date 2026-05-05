#!/bin/bash

if [ $# -eq 0 ]
  then
    echo "Please include a branch name: ./review-pull-requets.sh the-branch-name"s
    exit -2
fi


git reset --hard
git checkout ci-pipeline
git pull
git checkout $1
git pull
npm install
npm run storybook