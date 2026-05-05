#!/bin/bash

NEW_ORG=$1
NEW_APP=$2
NEW_LIB=$3
NEW_APP_COMPONENT=$(python -c "print(\"$NEW_APP\".capitalize())")
NEW_COMPONENT=$(python -c "print(\"$NEW_LIB\".capitalize())")
echo "$NEW_ORG: $NEW_APP - $NEW_LIB - $NEW_COMPONENT"

replaceAllInFile() {
    sed -i .bak "s/$1/$2/g" $3
    rm $3.bak
}

changeOrg() {
    replaceAllInFile spud $NEW_ORG package.json
    replaceAllInFile spud $NEW_ORG README.md
    replaceAllInFile Spud $NEW_ORG README.md
    replaceAllInFile spud $NEW_ORG nx.json
    replaceAllInFile bridgephase $NEW_ORG libs/girder/package.json
}

changeApp() {
    replaceAllInFile potato $NEW_APP jest.config.js
    replaceAllInFile potato $NEW_APP nx.json
    replaceAllInFile potato $NEW_APP workspace.json

    replaceAllInFile potato $NEW_APP apps/potato/jest.config.js
    replaceAllInFile potato $NEW_APP apps/potato/src/index.html
    replaceAllInFile potato $NEW_APP_COMPONENT apps/potato/src/index.html
    replaceAllInFile potato $NEW_APP_COMPONENT apps/potato/src/app/app.spec.tsx
    replaceAllInFile potato $NEW_APP_COMPONENT apps/potato/src/app/app.tsx

    mv apps/potato apps/$NEW_APP

    replaceAllInFile potato $NEW_APP apps/potato-e2e/cypress.json
    replaceAllInFile potato $NEW_APP apps/potato-e2e/src/integration/app.spec.ts
    replaceAllInFile potato $NEW_APP apps/potato-e2e/src/integration/app/app.spec.ts

    mv apps/potato-e2e apps/$NEW_APP-e2e
}

changeLibrary() {
    replaceAllInFile girder $NEW_LIB jest.config.js
    replaceAllInFile girder $NEW_LIB nx.json
    replaceAllInFile girder $NEW_LIB package.json
    replaceAllInFile girder $NEW_LIB README.md
    replaceAllInFile Girder $NEW_COMPONENT README.md
    replaceAllInFile girder $NEW_LIB tsconfig.base.json
    replaceAllInFile girder $NEW_LIB workspace.json

    replaceAllInFile girder $NEW_LIB apps/girder-e2e/cypress.json
    replaceAllInFile girder $NEW_LIB apps/girder-e2e/src/integration/girder/girder.spec.ts
    replaceAllInFile Girder $NEW_COMPONENT apps/girder-e2e/src/integration/girder/girder.spec.ts
    mv apps/girder-e2e/src/integration/girder/girder.spec.ts apps/girder-e2e/src/integration/girder/$NEW_LIB.spec.ts
    mv apps/girder-e2e apps/$NEW_LIB-e2e
    mv apps/$NEW_LIB-e2e/src/integration/girder  apps/$NEW_LIB-e2e/src/integration/$NEW_LIB

    replaceAllInFile girder $NEW_LIB libs/girder/jest.config.js
    replaceAllInFile girder $NEW_LIB libs/girder/package.json
    replaceAllInFile girder $NEW_LIB libs/girder/README.md
    replaceAllInFile girder $NEW_LIB libs/girder/src/index.ts
    
    replaceAllInFile girder $NEW_LIB libs/girder/src/lib/girder.spec.tsx
    replaceAllInFile Girder $NEW_COMPONENT libs/girder/src/lib/girder.spec.tsx
    mv libs/girder/src/lib/girder.spec.tsx libs/girder/src/lib/$NEW_LIB.spec.tsx

    replaceAllInFile girder $NEW_LIB libs/girder/src/lib/girder.stories.tsx
    replaceAllInFile Girder $NEW_COMPONENT libs/girder/src/lib/girder.stories.tsx
    mv libs/girder/src/lib/girder.stories.tsx libs/girder/src/lib/$NEW_LIB.stories.tsx

    replaceAllInFile girder $NEW_LIB libs/girder/src/lib/girder.tsx
    replaceAllInFile Girder $NEW_COMPONENT libs/girder/src/lib/girder.tsx
    mv libs/girder/src/lib/girder.tsx libs/girder/src/lib/$NEW_LIB.tsx

    mv libs/girder/src/lib/girder.module.scss libs/girder/src/lib/$NEW_LIB.module.scss

    rm -rf libs/girder/src/lib/__snapshots__

    mv libs/girder libs/$NEW_LIB
}

git reset --hard
rm package-lock.json
changeOrg
changeApp
changeLibrary
git add apps/
git add package.json
git add libs/girder/package.json
git add README.md
git add nx.json
git add jest.config.js
git add workspace.json
git add tsconfig.base.json
git add libs/
npm install
git add package-lock.json