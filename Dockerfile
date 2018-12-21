
FROM java:8  
COPY /target/cc-blockchain-api-0.0.1-SNAPSHOT.war /target/cc-blockchain-api-0.0.1-SNAPSHOT.war
WORKDIR server/target
CMD ["java","-jar","/target/cc-blockchain-api-0.0.1-SNAPSHOT.war"]



#FROM ubuntu:latest

#ENV TOMCAT_VERSION 8.5.35


# Fix sh
#RUN rm /bin/sh && ln -s /bin/bash /bin/sh

# Install dependencies
#RUN apt-get update && \
#apt-get install -y git build-essential curl wget iputils-ping software-properties-common

# Install JDK 8
#RUN \
#echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | debconf-set-selections && \
#add-apt-repository -y ppa:webupd8team/java && \
#apt-get update && \
#apt-get install -y oracle-java8-installer wget unzip tar && \
#rm -rf /var/lib/apt/lists/* && \
#rm -rf /var/cache/oracle-jdk8-installer

# Define commonly used JAVA_HOME variable
#ENV JAVA_HOME /usr/lib/jvm/java-8-oracle

# Install Maven
#RUN apt-add-repository universe
#RUN apt-get update
#RUN apt-get install -y maven

# Get Tomcat
#RUN wget --quiet --no-cookies http://apache.rediris.es/tomcat/tomcat-8/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz -O /tmp/tomcat.tgz && \
#tar xzvf /tmp/tomcat.tgz -C /opt && \
#mv /opt/apache-tomcat-${TOMCAT_VERSION} /opt/tomcat

# Define CATALINA variable
#ENV CATALINA_HOME /opt/tomcat
#ENV CATALINA_BASE /opt/tomcat
#ENV PATH $PATH:$CATALINA_HOME/bin



#RUN echo qwerty-123 | sudo -S su - ireslab04 -c "cp /var/sendx.war $CATALINA_HOME/webapps/sendx.war"
#COPY /target/cc-blockchain-api-0.0.1-SNAPSHOT.war $CATALINA_HOME/webapps/cc-blockchain-api-0.0.1-SNAPSHOT.war


# Expose port 8585 8009
#EXPOSE 8585
#EXPOSE 8009
#EXPOSE 8180

#WORKDIR $CATALINA_HOME

# Start Tomcat
#CMD ["catalina.sh", "run"]
