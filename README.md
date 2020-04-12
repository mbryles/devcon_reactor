# WebFlux DevConnector

## Description
Project Reactor port of the DevCon project from Brad Traversy's Udemy course found [here](https://www.udemy.com/course/mern-stack-front-to-back/learn/lecture/10055258#overview).

Last year I took Brad's class and found it to be a great way to learn ReactJS app-building concepts. However,
Brad's class focuses on nodejs on the backend and I'm a Java/JVM developer, so I decided to port the backend
to [Spring's Webflux Framework](https://projectreactor.io/).


## Getting Started

Note that all development and testing was done on macOS Catalina (10.5.4) and it is quite likely that these bootstrap 
steps will not work exactly as indicated.

### Prerequisites

1. Ensure that Java 11 is installed:
```
$ java -version
openjdk version "11.0.6" 2020-01-14
OpenJDK Runtime Environment AdoptOpenJDK (build 11.0.6+10)
OpenJDK 64-Bit Server VM AdoptOpenJDK (build 11.0.6+10, mixed mode)
```
2. Ensure that node 13 is installed:

```
$ node --version
v13.12.0
```

3. Ensure that npm 6 is installed:
```
npm --version
6.14.4
```

4. For local testing, ensure that mongodb v4.2.x is installed :
```
$ mongod --version
db version v4.2.1
git version: edf6d45851c0b9ee15548f0f847df141764a317e
allocator: system
modules: none
build environment:
    distarch: x86_64
    target_arch: x86_64
```

### Installation

This project uses a Gradle wrapper, so to build and run the unit tests for the backend or server application, simply
execute the following command:

```
$ ./gradlew clean build
```

To build the frontend code (and install the distribution to the server app), complete the following steps:
```
$ cd client
$ npm run build
```
Note: see the usual, out-of-the-box npm commands [here](client/README.md)

End with an example of getting some data out of the system or using it for a little demo

## Testing

### Unit Tests

Unit tests are dead simple to run:

```
$ ./gradlew test
```

### Integration Tests

If you have mongodb up and running, you can run the integration tests too:

```
$ ./gradlew test -Dintegration.test=true
```

## Deployment

There are several options for deployment.

To run the app locally, use the spring boot plugin (make sure mongodb is running!):
```
$ ./gradlew bootRun
```

Alternatively, use the '-jar' switch of the java command:
```
$ java -jar build/lib/devcon-0.0.1-SNAPSHOT.jar
```

For local ui development, navigate to the client directory and use npm to run the client code:

```
$ cd client
$ npm run dev
```

The app should import cleanly into your preferred IDE (but note that only IntelliJ was tested) and can be run
in debug mode that way as well.

For a full-fledged distribution, run the client build first and the server build second:

```
$ cd client
$ npm run build
$ cd ..
$ ./gradlew clean build
```

A convenient Heroku deployment script is included.

## Technology Stack

The WebFlux DevConnector app is built on top of the Spring Boot framework leveraging Projector Reactor's WebFlux 
framework. 

* [Spring Boot](https://spring.io/projects/spring-boot) - For the Web/REST App, Data abstraction, endpoint metadata, and Security
* [Project Reactor](https://projectreactor.io/) - For the reactive handlers and routers
* [Lombok](https://projectlombok.org/) - Used to generate getters, setters, builders
* [Gradle](https://gradle.org/) - For building and for dependency management (via the wrapper implementation)
* [Spock](http://spockframework.org/) - For testing and mocking
* [MongoDB](https://www.mongodb.com/) - For the datastore
* [ReactJS](https://reactjs.org/) - For the client code
* [Heroku](https://www.heroku.com/home) - For deployment testing in the wild
* [MongoDB Atlas](https://www.mongodb.com/cloud/atlas) - For db testing in the wild

## License

This project is licensed under the MIT License

## Acknowledgments
* As previously mentioned, this is port of the backend code from [Brad Traversy's](https://www.traversymedia.com/) excellent 
Udemy [course](https://www.udemy.com/course/mern-stack-front-to-back/learn/lecture/10055258#overview) on MERN stack development. 
Find the source code for this course [here](https://github.com/bradtraversy/devconnector_2.0)
* [Matt Raible's](https://raibledesigns.com/) series of posts on Reactive programming with ReactJS over at 
[Okta's Developer Blog](https://developer.okta.com/blog/) were invaluable resources

