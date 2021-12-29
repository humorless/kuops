FROM openjdk:8-alpine

COPY target/uberjar/kuops.jar /kuops/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/kuops/app.jar"]
