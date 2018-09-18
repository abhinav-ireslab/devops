FROM ubuntu:latest

# Fix sh
RUN rm /bin/sh && ln -s /bin/bash /bin/sh

# Install dependencies
RUN apt-get update && \
apt-get install -y git build-essential curl wget iputils-ping software-properties-common

# Install JDK 8
RUN \
echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | debconf-set-selections && \
add-apt-repository -y ppa:webupd8team/java && \
apt-get update && \
apt-get install -y oracle-java8-installer wget unzip tar && \
rm -rf /var/lib/apt/lists/* && \
rm -rf /var/cache/oracle-jdk8-installer


# Install Tomcat 8

RUN apt-get install tomcat8

RUN cp /target/cc-blockchain-api-0.0.1-SNAPSHOT.war /var/opt/tomcat8/webapps/coinclaim.war

# Expose port 8080 8009
EXPOSE 8080
EXPOSE 8009
EXPOSE 8180

WORKDIR /var/opt/tomcat8

# Start Tomcat
CMD ["catalina.sh", "run"]