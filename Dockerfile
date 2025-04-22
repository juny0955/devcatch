FROM openjdk:17-jdk-slim

RUN apt-get update && apt-get install -y --no-install-recommends \
      wget unzip libnss3 libatk-bridge2.0-0 libgbm1 \
    && rm -rf /var/lib/apt/lists/*

# Chrome 설치
RUN wget -qO /tmp/chrome-shell.zip \
      https://storage.googleapis.com/chrome-for-testing-public/135.0.7049.95/linux64/chrome-headless-shell-linux64.zip \
    && unzip /tmp/chrome-shell.zip -d /opt \
    && mv /opt/chrome-headless-shell-linux64 /opt/headless-shell \
    && ln -s /opt/headless-shell/chrome-headless-shell /usr/bin/headless-shell \
    && rm /tmp/chrome-shell.zip

# Chromedriver 설치
RUN wget -qO /tmp/chromedriver.zip \
      https://storage.googleapis.com/chrome-for-testing-public/135.0.7049.95/linux64/chromedriver-linux64.zip \
    && unzip /tmp/chromedriver.zip -d /usr/local/bin \
    && chmod +x /usr/local/bin/chromedriver \
    && rm /tmp/chromedriver.zip

# 애플리케이션 설정
ARG JAR_FILE_PATH=/build/libs/*.jar
COPY $JAR_FILE_PATH app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]