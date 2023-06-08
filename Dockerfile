FROM ubuntu:20.04

# Update the package lists and install any updates
RUN apt-get update && apt-get upgrade -y

# Install the required packages
