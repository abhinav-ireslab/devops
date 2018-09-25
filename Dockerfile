FROM java:8  
COPY /server/target/cc-blockchain-api-0.0.1-SNAPSHOT.war /server/target/cc-blockchain-api-0.0.1-SNAPSHOT.war
WORKDIR server/target
CMD ["java","-jar","/server/target/cc-blockchain-api-0.0.1-SNAPSHOT.war"]