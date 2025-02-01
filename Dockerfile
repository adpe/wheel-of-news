FROM eclipse-temurin:21
COPY target/wheel-of-news-*.jar /usr/app/app.jar
RUN useradd -m wheel-of-news
USER wheel-of-news
RUN mkdir /home/wheel-of-news/.wheel-of-news
EXPOSE 8080
CMD ["java", "-jar", "/usr/app/app.jar"]
HEALTHCHECK CMD curl --fail --silent localhost:8080/actuator/health | grep UP || exit 1