FROM maven:3.5-jdk-8-onbuild-alpine
COPY api-key /usr/src/app/target/api-key
CMD ["java","-jar","/usr/src/app/target/operator-estimator-jar-with-dependencies.jar"]
