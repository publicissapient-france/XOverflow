FROM java:8-jre

ADD xoverflow.jar /project/xoverflow.jar

EXPOSE 80

ENTRYPOINT ["java", "-jar", "/project/xoverflow.jar ${ES_URL}"]
