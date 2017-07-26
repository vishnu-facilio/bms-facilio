echo "this will the script kill the server"
sudo killall -9 java
sudo rm -rf /home/ubuntu/apache-tomcat-9.0.0.M21/webapps/bms
sudo rm -rf /home/ubuntu/apache-tomcat-9.0.0.M21/webapps/bms.war