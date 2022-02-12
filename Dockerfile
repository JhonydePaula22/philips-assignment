FROM openjdk:11
ADD ./target/philips-code-challenge-0.0.1-SNAPSHOT.jar /usr/src/philips-code-challenge-0.0.1-SNAPSHOT.jar
WORKDIR usr/src
ENTRYPOINT ["java","-jar", "philips-code-challenge-0.0.1-SNAPSHOT.jar"]