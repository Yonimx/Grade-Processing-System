# Builds and runs GradeFlow on Tomcat 11 + Java 21 (used for online hosting, e.g. Render).
# Eclipse does not use this file; it only matters when deploying online.

FROM tomcat:11.0-jdk21

# Deploy the web content as the ROOT app so it opens at the site's main address
RUN rm -rf /usr/local/tomcat/webapps/*
COPY src/main/webapp /usr/local/tomcat/webapps/ROOT

# Compile the Java classes into WEB-INF/classes (same thing Eclipse does)
COPY src/main/java /tmp/src
RUN mkdir -p /usr/local/tomcat/webapps/ROOT/WEB-INF/classes \
 && javac -encoding UTF-8 \
      -cp /usr/local/tomcat/lib/servlet-api.jar \
      -d /usr/local/tomcat/webapps/ROOT/WEB-INF/classes \
      $(find /tmp/src -name "*.java") \
 && rm -rf /tmp/src

# Keep memory use within small free hosting plans
ENV CATALINA_OPTS="-XX:MaxRAMPercentage=70"

EXPOSE 8080

# Hosting services give the port in $PORT; fall back to 8080 when it is not set
CMD sed -i "s/port=\"8080\"/port=\"${PORT:-8080}\"/g" conf/server.xml && exec catalina.sh run
