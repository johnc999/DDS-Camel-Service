FROM registry.access.redhat.com/ubi8/openjdk-11-runtime:latest

MAINTAINER "Johnathan Ingram <johnathan.ingram@ato.gov.au>"

ENV XMS 64m
ENV XMX 256m
ENV OPTS ""

# Install Service
RUN mkdir -p /opt/camel-service/etc && \
    mkdir /opt/camel-service/agent && \
    mkdir /opt/camel-service/lib

COPY target/configuration/* /opt/camel-service/etc/
COPY target/*-j2se-*.jar /opt/camel-service/camel-service-j2se.jar
COPY target/lib/* /opt/camel-service/lib/

COPY target/agent/jmx_prometheus_javaagent.jar /opt/camel-service/agent/jmx_prometheus_javaagent.jar

ENTRYPOINT java \
           -Xms${XMS} -Xmx${XMX} \
           ${OPTS} \
           -XX:+DisableExplicitGC \
           -javaagent:/opt/camel-service/agent/jmx_prometheus_javaagent.jar=9779:/opt/camel-service/etc/prometheus-agent-config.yml \
           -Dintegration-config-path=/opt/camel-service/etc \
           -jar \
           /opt/camel-service/camel-service-j2se.jar