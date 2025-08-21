# Briscala
This project aims to create a DSL for generating typical card games
Documentation is available at [docs](https://agostax0.github.io/PPS-24-Briscala/).


## How to run

### Ubuntu

We confirmed that the project works on Ubuntu 24.04 LTS with OpenJDK 21.0.7.

1. To run the project download the release and run the following command:
   ```bash
   java -jar Briscala_v200.jar
   ```
   2. (Alternative) Download the repository and run the following command:
   ```bash
   sbt run
   ```

### Windows

We confirmed that the project works on Windows 10/11 with Java Runtime class file version >=66.0.

1. To run the project download the release and run the following command:
   ```bash
   java -jar Briscala_v200.jar
   ```
   2. (Alternative) Download the repository and run the following command:
   ```bash
   sbt run
   ```
   
## Usage

Upon execution on the terminal will appear a list of pre-made games.
```
Choose any of these pre-made games: [ briscola marafone rovescino custom ]
```


Upon selecting a game, the user will be asked if cards should always be visible.
```
Should cards be always visible? [Y / n]
```
