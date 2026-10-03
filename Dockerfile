FROM eclipse-temurin:25-jdk

WORKDIR /app

COPY src /app/src
COPY lib /app/lib
COPY forntend /app/forntend

RUN mkdir -p /app/out \
    && javac -cp "/app/lib/*" -d /app/out $(find /app/src -name "*.java")

WORKDIR /var/data

EXPOSE 8080

CMD ["sh", "-c", "java -cp '/app/out:/app/lib/*' database.DatabaseSetup && exec java -cp '/app/out:/app/lib/*' server.ApiServer"]
