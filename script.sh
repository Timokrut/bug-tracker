sudo rm -rf /var/lib/tomcat9/webapps/
sudo mkdir -p /var/lib/tomcat9/webapps/
sudo cp target/bug-tracker.war /var/lib/tomcat9/webapps/
sudo systemctl restart tomcat9

echo "Done"
