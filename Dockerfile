FROM phusion/baseimage:0.9.17

MAINTAINER Ben Ripkens <bripkens@gmail.com>

# Ensure that the operating system is up to date
RUN apt-get update && \
    apt-get upgrade -y -o Dpkg::Options::="--force-confold" && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/**

# Install Oracle Java 8
RUN curl --silent \
         --location \
         --retry 3 \
         --header "Cookie: oraclelicense=accept-securebackup-cookie;" \
         "http://download.oracle.com/otn-pub/java/jdk/8u65-b17/jdk-8u65-linux-x64.tar.gz" \
         | gunzip | tar x -C /usr/lib/ && \
         update-alternatives --install /usr/bin/java java /usr/lib/jdk1.8.0_65/bin/java 100
ENV JAVA_HOME /usr/lib/jdk1.8.0_60 \
    PATH /usr/lib/jdk1.8.0_60/bin:$PATH

ARG revision
ARG now

LABEL com.bripkens.ha.revision=${revision} \
      com.bripkens.ha.buildDate=${now}

# include the information as possible debugging output in the executing application
ENV HA_REVISION=${revision} \
    HA_BUILD_DATE=${now}

# ensure that configs can be mounted
RUN mkdir -p /opt/health-check-adapter/config
VOLUME /opt/health-check-adapter/config

# Copy application and service definition
RUN mkdir /etc/service/health-check-adapter
ADD deployment/service-definition /etc/service/health-check-adapter/run
COPY target/scala-2.11/health-check-adapter.jar /opt/health-check-adapter/health-check-adapter.jar

# Default command. Assumes that the config is located next to the JAR
CMD ["/sbin/my_init"]