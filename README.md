# GLCD Emulator Client for Java

[![Build Status](https://travis-ci.org/ribasco/glcd-emulator-client-java.svg?branch=master)](https://travis-ci.org/ribasco/glcd-emulator-client-java) [![Maven Central](https://img.shields.io/maven-central/v/com.ibasco.glcdemulator/glcd-emulator-client.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.ibasco.glcdemulator%22%20AND%20a:%22glcd-emulator-client%22)

Java client library for glcd-emulator (See: https://github.com/ribasco/glcd-emulator)


# Installation

From source

```bash
git clone https://github.com/ribasco/glcd-emulator-client-java.git

cd glcd-emulator-client-java

mvn install
```

# Usage

Add the following dependency to your maven pom.xml file

```xml
<dependency>
  <groupId>com.ibasco.glcdemulator</groupId>
  <artifactId>glcd-emulator-client</artifactId>
  <version>1.0.1-alpha</version>
</dependency>
```

# Example 

Refer to the [TCP example code](https://github.com/ribasco/glcd-emulator-client-java/blob/master/src/examples/java/GlcdClientTcpExample.java)

To run the example via maven:

```bash
mvn clean compile exec:java -Dexec.mainClass="GlcdClientTcpExample"
```
