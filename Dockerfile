FROM openjdk:17-jdk

RUN apt-get update && apt-get install -y --no-install-recommends \
        wget gnupg unzip \
        libnss3 libatk-bridge2.0-0 libgbm1 libgtk-3-0 \
        libx11-xcb1 libxrandr2 libxdamage1 libxcomposite1 libxss1 libasound2 \
    && rm -rf /var/lib/apt/lists/*

# Chrome 설치
RUN wget -qO- https://dl.google.com/linux/linux_signing_key.pub \
        | gpg --dearmor -o /usr/share/keyrings/google.gpg \
    && echo "deb [signed-by=/usr/share/keyrings/google.gpg] \
        https://dl.google.com/linux/chrome/deb/ stable main" \
        > /etc/apt/sources.list.d/google.list \
    && apt-get update \
    && apt-get install -y --no-install-recommends \
        google-chrome-stable=135.0.7049.84-1 \
    && rm -rf /var/lib/apt/lists/*

# Chromedriver 설치
RUN wget -qO /tmp/chromedriver.zip \
        https://chromedriver.storage.googleapis.com/135.0.7049.84/chromedriver_linux64.zip \
    && unzip /tmp/chromedriver.zip -d /usr/local/bin \
    && chmod +x /usr/local/bin/chromedriver \
    && rm /tmp/chromedriver.zip

# 환경 변수 설정
ENV CHROME_OPTIONS="--headless --no-sandbox --disable-dev-shm-usage"

# 애플리케이션 설정
ARG JAR_FILE_PATH=/build/libs/*.jar
COPY $JAR_FILE_PATH app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]