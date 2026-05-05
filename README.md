# USCIS - React Beacon

This project was generated using [Nx](https://nx.dev). The goal of this project is to have a starting ground for a UI application along with a UI library. This is developed as a mono repo which means that the library and application both are stored on the same repo.

## Getting Started

- `npm install` should install all required tools, after that each project has its own set of tasks
  - `npm install` will make updates to the package-lock.json. DO NOT check in these changes unless there are specific updates because it will override any manaul package dependencies updates that may be present. If unsure, use `npm ci` to install the packages instead.

## Library - Beacon

To run the storybook for the library, run `npm run storybook`, the rest of the tasks can be viewed in `package.json`.

## Resolving Common Errors

"Cannot find module ... and its corresponding type declarations" in Visual Studio Code

This may be caused by your IDE using the incorrect Typescript version and may be fixed by configuring it to use the workspace version:

1. Navigate to any Typescript file (i.e. any file that ends with .ts or .tsx)
2. Open Command Palette (CMD + SHIFT + P)
3. Choose "Typescript: Select Typescript Version..." command
4. Choose "Use Workspace Version" option

## After Merging

Update elis-apps with the new react-beacon version (even if the change was needed for a microservice, elis-apps should
be kept up to date with the latest react-beacon at all times):
https://d2wcj5mh2t50ka.cloudfront.net/storybook/InternalApp/index.html?path=/story/getting-started-lessons-best-practices-maintenance--upgrading-react-beacon
