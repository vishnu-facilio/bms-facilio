FROM tomcat:9-jdk8
RUN rm -rf /usr/local/tomcat/webapps/*
ADD target/ROOT.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080
CMD ["catalina.sh", "run"]