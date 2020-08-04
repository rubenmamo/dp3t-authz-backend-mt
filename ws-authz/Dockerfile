FROM openjdk:11.0.7-jre-slim
MAINTAINER Malcolm Spiteri <malcolm.spiteri@gov.mt>

ARG targetenv=dev

# Install ws
RUN useradd ws

WORKDIR /home/ws/

# Create skeleton
RUN mkdir -p /home/ws/bin && \
    mkdir -p /home/ws/conf && \
    mkdir -p /home/ws/archive && \
    mkdir -p /home/ws/log && \
    mkdir -p /home/ws/tmp

# Copy binary
ADD ./ws/bin/dpppt-authz-backend-1.0.0.jar /home/ws/bin/dpppt-authz-backend.jar

# Copy configs
ADD ./ws/conf/dpppt-authz-backend-ws-logback-${targetenv}.xml /home/ws/conf/dpppt-authz-backend-ws-logback.xml
ADD ./ws/conf/dpppt-authz-backend-ws-${targetenv}.properties /home/ws/conf/dpppt-authz-backend-ws.properties

RUN chown -R ws:ws /home/ws

# Access to webinterface
EXPOSE 8080

CMD java -jar $JAVA_OPTS -Dlogging.config=/home/ws/conf/dpppt-authz-backend-ws-logback.xml -Dspring.config.location=/home/ws/conf/dpppt-authz-backend-ws.properties /home/ws/bin/dpppt-authz-backend.jar
