FROM maven:3.5-jdk-8-onbuild-alpine
ENV WEATHER_API_KEY=$WEATHER_API_KEY_ARG
CMD ["java","-jar","/usr/src/app/target/operator-estimator-jar-with-dependencies.jar"]
