echo "this will the script restarting the server"
sudo killall -9 java
sudo rm -rf /home/ubuntu/apache-tomcat-9.0.0.M21/webapps/bms
sudo sh /home/ubuntu/apache-tomcat-9.0.0.M21/bin/catalina.sh start