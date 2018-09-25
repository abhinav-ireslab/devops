FROM java:8  
COPY /target/cc-blockchain-api-0.0.1-SNAPSHOT.war /target/cc-blockchain-api-0.0.1-SNAPSHOT.war
WORKDIR server/target
CMD ["java","-jar","/target/cc-blockchain-api-0.0.1-SNAPSHOT.war"]