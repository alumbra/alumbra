# How to contribute

Contributions are always welcome and it should be as seamless as possible for
you to land patches in master.

## Getting Started

- Make sure you have a [GitHub account][github].
- Submit an issue outlining your bug/feature (unless there is already one). This
  way we can discuss possible solutions/implementations and make sure everyone
  is on the same page.
- Fork the repository.

[github]: https://github.com/signup/free

## Making Changes

- Create a topic branch off of `master`.
- Commit logical units, try to avoid mixing of changes to different logical
  parts of the project.
- Make sure your commit messages have the proper format (see existing commits),
  starting with `[#<issue number>] ...`. Ideally, the commit text should also
  contain a short description of the feature/bug it is addressing.
- Make sure you have added tests for your changes.
- Run the complete testsuite (`lein test`) and see it pass.

## Submitting Changes

- Push your changes to your topic branch.
- Submit a Pull Request to the GitHub repository, mentioning the original issue
  in the description text.
- The Pull Request will be discussed and feedback provided.

## Additional Resources

- [GitHub Help](https://help.github.com/)
- [Using Pull Requests](https://help.github.com/articles/using-pull-requests/)
