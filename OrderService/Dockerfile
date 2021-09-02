FROM openjdk:11-jdk-alpine

LABEL maintainer="zhouquan <zhouquan2k@gmail.com>"

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo 'Asia/Shanghai' >/etc/timezone


# Set environment variables.
ENV HOME /root

# Define working directory.
WORKDIR /root


# Define default command.
CMD ["bash"]

ARG MAIN_PROJECT
ARG JAR_FILE=target/*.jar
COPY ./${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","app.jar"]