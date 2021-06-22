FROM openjdk:latest
VOLUME /tmp
COPY target/demo-0.0.1-SNAPSHOT.jar javaspringapi.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "javaspringapi.jar"]
# For Spring-Boot project, use the entrypoint below to reduce Tomcat startup time.
#ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar javaspringapi.jar
