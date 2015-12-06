FROM phusion/baseimage:0.9.17

MAINTAINER Ben Ripkens <bripkens@gmail.com>

# Install Oracle Java 8
RUN curl --silent \
         --location \
         --retry 3 \
         --header "Cookie: oraclelicense=accept-securebackup-cookie;" \
         "http://download.oracle.com/otn-pub/java/jdk/8u65-b17/jdk-8u65-linux-x64.tar.gz" \
         | gunzip | tar x -C /usr/lib/ && \
         update-alternatives --install /usr/bin/java java /usr/lib/jdk1.8.0_65/bin/java 100
ENV JAVA_HOME /usr/lib/jdk1.8.0_60
ENV PATH /usr/lib/jdk1.8.0_60/bin:$PATH

ARG revision
ARG now

LABEL com.bripkens.ha.revision=${revision} \
      com.bripkens.ha.buildDate=${now}

# include the information as possible debugging output in the executing application
ENV HA_REVISION=${revision} \
    HA_BUILD_DATE=${now}

# Copy application
WORKDIR /opt/health-check-adapter
COPY target/scala-2.11/health-check-adapter.jar /opt/health-check-adapter/health-check-adapter.jar

# Default command. Assumes that the config is located next to the JAR
CMD java -jar health-check-adapter.jar config.yml