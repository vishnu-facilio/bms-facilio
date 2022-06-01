FROM tomcat:9-jdk8
RUN rm -rf /usr/local/tomcat/webapps/*
ADD target/ROOT.war /usr/local/tomcat/webapps/ROOT.war
RUN unzip /usr/local/tomcat/webapps/ROOT.war -d /usr/local/tomcat/webapps/ROOT
RUN sed -i 's/db.host=localhost/db.host=bms-db/g' /usr/local/tomcat/webapps/ROOT/WEB-INF/classes/conf/awsprops.properties
RUN apt-get update && apt-get install -y iputils-ping
RUN wget https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar> /usr/local/opentelemetry-javaagent.jar
EXPOSE 8080
CMD ["catalina.sh", "run"]