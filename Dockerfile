FROM openjdk:21-slim

LABEL maintainer="AutoRia Proj"

RUN apt-get update && apt-get install -y bash && rm -rf /var/lib/apt/lists/*

RUN mkdir /app
WORKDIR /app
