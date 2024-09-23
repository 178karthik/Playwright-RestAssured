FROM eclipse-temurin:21-jdk

RUN apt-get update \
  && apt-get install -y ca-certificates curl git --no-install-recommends \
  && rm -rf /var/lib/apt/lists/*

# common for all images
ENV MAVEN_HOME /usr/share/maven

COPY --from=maven:3.9.6-eclipse-temurin-11 ${MAVEN_HOME} ${MAVEN_HOME}
COPY --from=maven:3.9.6-eclipse-temurin-11 /usr/local/bin/mvn-entrypoint.sh /usr/local/bin/mvn-entrypoint.sh
COPY --from=maven:3.9.6-eclipse-temurin-11 /usr/share/maven/ref/settings-docker.xml /usr/share/maven/ref/settings-docker.xml

RUN ln -s ${MAVEN_HOME}/bin/mvn /usr/bin/mvn

ARG MAVEN_VERSION=3.9.6
ARG USER_HOME_DIR="/root"
ENV MAVEN_CONFIG "$USER_HOME_DIR/.m2"

ENTRYPOINT ["/usr/local/bin/mvn-entrypoint.sh"]

# Argument with default value
#ARG suiteFile=repo-test.xml
#ARG env=uat
# Copy code from local to image
LABEL version="1.1"

ENV env=uat
ENV targetProfile=self-tests

COPY ./ /app
# Specify working directory
WORKDIR /app
# Execute command at creation of image
RUN mvn clean compile
# Command to be executed at start of container
#CMD mvn test -Dsurefire.suiteXmlFiles=repo-test.xml -Denv=$env

CMD mvn clean install -P$targetProfile -Denv=$env