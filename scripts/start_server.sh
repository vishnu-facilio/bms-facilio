echo "this will the script start the server"
sudo killall -9 java
sudo unzip /home/ubuntu/apache-tomcat-9.0.0.M21/webapps/bms.war -d /home/ubuntu/apache-tomcat-9.0.0.M21/webapps/bms
sudo sh /home/ubuntu/apache-tomcat-9.0.0.M21/bin/catalina.sh start