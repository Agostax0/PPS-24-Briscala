# Testing
## Technologies 
Scala Test has been used for unit testing along with automated sbt testing using GitHub-native workflows.

## Methodology
We tried to follow a Test-Driven Development (TDD) approach, where tests are written before the actual implementation of the feature. This allows us to ensure that each feature works as intended and to catch bugs early in the development process.
After the implementation of a feature, we also checked the coverage of the tests using Scoverage, possibly improving the coverage by adding more tests.

## Coverage
Scoverage reported a coverage of ..% for the project which was our aim since the start.
The main culprits for lowering the coverage are some elements of the GUI, which are not easily testable, and some parts of *EngineController* which are not accessible for tests due to the way the code is structured.

## Example
We wrote an implementation of the *GameBuilder* trait called *SimpleGameBuilder*.
This class acts like a mockup for the *GameBuilder* with a less rigid structure, allowing us to better test the trait.

| [Previous Chapter](../6-implementation/index.md) | [Index](../index.md) | [Next Chapter](../8-retrospective/index.md) |