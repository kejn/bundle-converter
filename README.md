[![build](https://travis-ci.org/kejn/bundle-converter.svg?branch=master)](https://travis-ci.org/kejn/bundle-converter)  [![codecov](https://codecov.io/gh/kejn/bundle-converter/branch/master/graph/badge.svg)](https://codecov.io/gh/kejn/bundle-converter) [![maintainability](https://api.codeclimate.com/v1/badges/e98cf222ee4e301b88be/maintainability)](https://codeclimate.com/github/kejn/bundle-converter/maintainability)

## bundle-converter

Java library allowing easy conversion between `.properties` and `.xlsx` file formats.

![screen](https://raw.githubusercontent.com/kejn/bundle-converter/master/img/screen.png)

### Basic usage

There is an example commandline application  project which shows the basic usage of the *bundle-converter* API: [bundle-converter-cmd](https://github.com/kejn/bundle-converter-cmd). Explore the code to learn-by-example.

### Why would I even use it?

1. **Easy documentation maintenance**.  
Keep and manage all the texts used accross your application in a single file. All you have to do is just to convert your `.properties` files after a new release.
1. **Popular file format**.  
`.xlsx` files can be easily managed and are also well-known by most PC users, possibly: _your clients_. They will appreciate to always have the insight into what they see in the application and in case some text required to be corrected, their corrections could be done almost without any effort.

### Building

Open the project directory in command-line and execute:

- On Windows: `gradlew.bat build`
- On Unix: `./gradlew build`

### Add it to your project

Project dependency is available both on [jCenter](https://bintray.com/kejn/maven2/bundle-converter) and [Maven Central](https://mvnrepository.com/artifact/com.github.kejn/bundle-converter).

**Maven**

    <dependency>
        <groupId>com.github.kejn</groupId>
        <artifactId>bundle-converter</artifactId>
        <version>1.0.0</version>
    </dependency>


**Gradle**

    compile 'com.github.kejn:bundle-converter:1.0.0'

### Something's wrong!

In case something is not working as expected, feel free to create an [Issue](https://github.com/kejn/bundle-converter/issues) or [PullRequest](https://github.com/kejn/bundle-converter/pulls).
