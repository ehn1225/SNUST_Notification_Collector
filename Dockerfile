FROM adoptopenjdk/openjdk11:alpine-jre
COPY *.jar app.jar
COPY *.txt URLs.txt
ENTRYPOINT ["java", "-jar", "app.jar"]