# Base image - Java 21 aur Tomcat 11 
# already installed hoga is image mein
FROM tomcat:11.0-jdk21

# Tomcat ka default app delete karo
# Hamara app ROOT pe aayega
RUN rm -rf /usr/local/tomcat/webapps/ROOT

# Hamara WAR file copy karo server pe
# Maven build ke baad target folder mein
# SpendWise.war file hoti hai
COPY target/SpendWise.war /usr/local/tomcat/webapps/ROOT.war

# Port 8080 open karo
EXPOSE 8080

# Tomcat start karo
CMD ["catalina.sh", "run"]