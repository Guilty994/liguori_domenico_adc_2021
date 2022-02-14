
FROM alpine/git
WORKDIR /app
RUN git clone https://github.com/Guilty994/liguori_domenico_adc_2021.git

FROM maven:3.5-jdk-10-alpine
WORKDIR /app
COPY --from=0 /app/liguori_domenico_adc_2021 /app
RUN mvn package

FROM openjdk:10-jre-alpine
WORKDIR /app
ENV MASTERIP=127.0.0.1
ENV ID=0
COPY --from=1 /app/target/AnonymousChat-1.0-jar-with-dependencies.jar /app

CMD /usr/bin/java -jar AnonymousChat-1.0-jar-with-dependencies.jar -m $MASTERIP -id $ID