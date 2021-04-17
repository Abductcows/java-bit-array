# Contributing to Java BitArray

Thank you for taking an interest in this project. You can contribute by writing code to fix/extend the features or use/test the class itself. 

You should start by [creating an issue](https://github.com/Abductcows/java-bit-array/issues) and relaying your idea or findings. You can find issue templates [here](https://github.com/Abductcows/java-bit-array/issues/new/choose) but using them is not mandatory. 


### Bug reports

Create an [issue](https://github.com/Abductcows/java-bit-array/issues) with the [bug](https://github.com/Abductcows/java-bit-array/labels/bug) label and state the problem. A "good" bug report usually contains:
- A quick summary of the use case and context
- What you expected to see
- What actually happened
- Steps to reproduce the unexpected behaviour if not clear
- Code snippets, debugger messages or screenshots if appropriate

### Code contributions

This repository uses [Maven](https://maven.apache.org/) for project management and [SemVer 2.0](https://semver.org/) for version specification. Source file format is **UTF-8 CRLF** with **4 spaces** for indentation. Development takes place in the [dev](https://github.com/Abductcows/java-bit-array/tree/dev) branch and is eventually integrated in the next release.

Before writing any code, you should state your intent by creating an [issue](https://github.com/Abductcows/java-bit-array/issues) with the appropriate tag. You could also ask to be assigned to an existing issue. Upon confirmation, follow the usual fork-pull request pattern (and increment product version):

- [Fork](https://docs.github.com/en/github/getting-started-with-github/fork-a-repo#fork-an-example-repository) this repository (top right corner)
- [Clone](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository#cloning-a-repository-using-the-command-line) your new repository to your machine and make the code changes
- **Important:** increment version number in [pom.xml](https://github.com/Abductcows/java-bit-array/blob/dev/pom.xml) and Javadoc `@version` in compliance with [SemVer 2.0](https://semver.org/)
- [Commit and push](https://docs.github.com/en/github/managing-files-in-a-repository/adding-a-file-to-a-repository-using-the-command-line) your changes to your forked repository
- [Create a pull request](https://docs.github.com/en/github/collaborating-with-issues-and-pull-requests/creating-a-pull-request#creating-the-pull-request) from your repository's dev branch to this repository.

Please note that pull requests which do not comply with the rules aforementioned or make unnecessary whitespace modifications at unrelated locations will not be admitted unless revised. 

### License

By contributing to this repository, you agree that your contributions will be licensed under the project's license: Apache License, Version 2.0
