#!/bin/bash
version=$1
tag="Beacon version $version"

if [ -n "$(git status --porcelain)" ]; then
    echo "There are changes detected, adding the files"
    git add --all docs/
    git config user.name "Jenkins Pipeline"
    git config user.email "svc-jenkins@uscis.dhs.gov"
    git commit -m "cruise-jenkins: updating storybook for version $version"
    export GIT_SSH_COMMAND="ssh -oStrictHostKeyChecking=no"
    git push -u origin HEAD:master
else
    echo "no changes detected, not adding an additional commit";
fi

echo "Pushing a new tag for $version"
git tag -a $version -m "$tag"
git push origin $version
