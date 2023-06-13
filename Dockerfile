# Base image: Ubuntu 20.04
FROM ubuntu:20.04

ARG DEBIAN_FRONTEND=noninteractive

# Install necessary packages
RUN apt-get update 
RUN apt-get install -y apt-transport-https bison build-essential ca-certificates clang curl doxygen flex gcc gdb git gradle graphviz libboost1.71-all-dev libcppunit-dev libjsoncpp-dev libosgearth-dev libqt5opengl5-dev libwebkit2gtk-4.0-37 libxml2-dev lld make mongodb mpi-default-dev openjdk-8-jdk openscenegraph-plugin-osgearth perl python2 python3 python3-pip python-is-python2 qt5-qmake qtbase5-dev qtbase5-dev-tools qtchooser software-properties-common wget xterm zlib1g-dev

# Install Python packages
RUN python3 -m pip install --system --upgrade \
    jinja2 \
    matplotlib \
    numpy \
    pandas \
    posix_ipc \
    scipy \
    seaborn \
    webgme-bindings 

# Set up Java 8 (do we need this?)

# Download and extract portico
WORKDIR /home
RUN wget -O portico.tar.gz https://master.dl.sourceforge.net/project/portico/Portico/portico-2.1.0/portico-2.1.0-linux64.tar.gz?viasf=1 && \
    tar xf portico.tar.gz && \
    rm portico.tar.gz
ENV RTI_HOME="/home/portico-2.1.0"

# Download and set up Apache Archiva
# USER root
WORKDIR /opt
RUN wget -O archiva.tar.gz https://archive.apache.org/dist/archiva/2.2.5/binaries/apache-archiva-2.2.5-bin.tar.gz && \
    tar xf archiva.tar.gz && \
    rm archiva.tar.gz

COPY archiva.service /etc/systemd/system/
RUN systemctl enable archiva && \
    systemctl start archiva

# Expose the Archiva port
EXPOSE 8080 

# Set up Gradle
RUN mkdir /home/.gradle
COPY gradle.properties /home/.gradle/

# Clone and build CPSWT packages
RUN mkdir /home/cpswt
WORKDIR /home/cpswt
RUN git clone git@github.com:SimIntToolkit/cpswt-core.git && \
    cd cpswt-core/cpswt-core && \
    gradle wrapper --gradle-version=7.3 && \
    ./gradlew :utils:publish && \
    ./gradlew :root:publish && \
    ./gradlew :base-events:publish && \
    ./gradlew :coa:publish && \
    ./gradlew :config:publish && \
    ./gradlew :federate-base:publish && \
    ./gradlew :federation-manager:publish && \
    ./gradlew :fedmanager-host:publish

WORKDIR /home/cpswt/cpswt-core
RUN ./gradlew :utils:build && \
    ./gradlew :root:build && \
    ./gradlew :base-events:build && \
    ./gradlew :coa:build && \
    ./gradlew :config:build && \
    ./gradlew :federate-base:build && \
    ./gradlew :federation-manager:build && \
    ./gradlew :fedmanager-host:build
 

