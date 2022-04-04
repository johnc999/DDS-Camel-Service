Startup
java -agentlib:jdwp=transport=dt_socket,server=n,suspend=y,address=localhost:64820 -javaagent:./target/jmx_prometheus_javaagent.jar=9779:./configuration/Metrics/prometheus-agent-config.yml -jar ./target/Integration-Platform-0.0.1-SNAPSHOT.jar --spring.config.location=./src/main/resources/application.yml --spring.config.location=./configuration/bambora.properties --spring.config.location=./configuration/mq.properties

cd configuration/Metrics
docker-compose up

Endpoints
   Exposed Metrics        : http://localhost:9779
   Prometheus             : http://localhost:9090
   Grafana                : http://localhost:3000
   AlertManager           : http://localhost:9093
   Mock Request Bin       : http://localhost:9095/alerts?inspect
   Jaeger UI              : http://localhost:16686
   Jaeger Agent (Thrift)  : localhost 6831/udp

Graphana
   Username: admin
   Password: nimda

Prometheus Resources
https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html
https://www.tutorialworks.com/spring-boot-prometheus-micrometer/
https://docs.spring.io/spring-boot/docs/2.1.11.RELEASE/reference/html/production-ready-endpoints.html

Grafana Resources
https://github.com/alainpham/app-archetypes
https://blog.56k.cloud/provisioning-grafana-datasources-and-dashboards-automagically/
