##  GameSessionSimulator

This a simulator of gamesessions of 1Huddle backend. Records are 
read from CSV files (which follow a certain schema), converted to 
JSON and then fed to the HTTP endpoint of a **GameSessionRecorderService**, 
running elsewhere.

This simulator uses a 3rd party Load-Testing tool named gatling
( [here]() https://gatling.io/). The tool has many capabilities. We
are using only the scala-based DSL of it. For the prototype to run, that
is sufficient.

## Requirements

*   sbt (https://www.scala-sbt.org/)
*   java 1.8+
*   Scala 2.11.x
*   set SBT_HOME to wherever sbt is installed
*   set JAVA_HOME to wherever jdk 1.8+ is installed
*   set SCALA_HOME to wherever scala 2.11.x is installed
*   adjust PATH to point to $JAVA_HOME/bin, $JAVA_HOME/lib, $SCALA_HOME/bin, $SCALA_HOME/lib and $SBT_HOME

## How to build and run

*   checkout from github
*   move to the project folder, where it has been checked out
 
*   fire command at $ prompt: sbt <enter>
*   at the _sbt_ prompt, fire command: gatling test <enter>