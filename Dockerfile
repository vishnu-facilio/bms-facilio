FROM tomcat:9-jdk8
RUN rm -rf /usr/local/tomcat/webapps/*
ADD target/ROOT.war /usr/local/tomcat/webapps/ROOT.war
RUN unzip /usr/local/tomcat/webapps/ROOT.war -d /usr/local/tomcat/webapps/ROOT
RUN sed -i 's/db.host=localhost/db.host=bms-db/g' /usr/local/tomcat/webapps/ROOT/WEB-INF/classes/conf/awsprops.properties
EXPOSE 8080
CMD ["catalina.sh", "run"]